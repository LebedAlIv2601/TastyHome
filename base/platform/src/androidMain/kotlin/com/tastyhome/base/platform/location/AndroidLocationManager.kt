package com.tastyhome.base.platform.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location as NativeLocation
import android.location.LocationListener
import android.location.LocationManager as NativeLocationManager
import android.os.Build
import com.tastyhome.base.platform.permissions.Permission
import com.tastyhome.base.platform.permissions.PermissionManager
import com.tastyhome.base.platform.permissions.PermissionStatus
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

private const val SECONDS_10 = 10000L
private const val DISTANCE = 10f

internal class AndroidLocationManager(
    private val context: Context,
    private val permissionManager: PermissionManager,
) : LocationManager {

    private val locationManager by lazy {
        context.getSystemService(Context.LOCATION_SERVICE) as NativeLocationManager
    }

    private var currentListener: LocationListener? = null

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Location? {
        if (permissionManager.requestPermission(Permission.LOCATION) != PermissionStatus.GRANTED) {
            return null
        }
        return suspendCancellableCoroutine { continuation ->

            val provider = getBestProvider() ?: run {
                continuation.resume(null)
                return@suspendCancellableCoroutine
            }

            locationManager.getLastKnownLocation(provider)?.let { lastKnown ->
                continuation.resume(lastKnown.toLocation())
                return@suspendCancellableCoroutine
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                locationManager.getCurrentLocation(
                    provider,
                    null,
                    context.mainExecutor,
                ) { androidLocation ->
                    continuation.resume(androidLocation?.toLocation())
                }
            } else {
                val listener = object : LocationListener {
                    override fun onLocationChanged(androidLocation: NativeLocation) {
                        continuation.resume(androidLocation.toLocation())
                        locationManager.removeUpdates(this)
                    }

                    override fun onProviderDisabled(provider: String) {
                        continuation.resume(null)
                        locationManager.removeUpdates(this)
                    }
                }

                locationManager.requestSingleUpdate(provider, listener, null)

                continuation.invokeOnCancellation {
                    locationManager.removeUpdates(listener)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun observeLocation(): Flow<Location> = callbackFlow {
        if (permissionManager.requestPermission(Permission.LOCATION) != PermissionStatus.GRANTED) {
            close()
            return@callbackFlow
        }

        val provider = getBestProvider() ?: run {
            close()
            return@callbackFlow
        }

        val listener = LocationListener { androidLocation ->
            trySend(androidLocation.toLocation())
        }

        currentListener = listener

        locationManager.requestLocationUpdates(
            provider,
            SECONDS_10,
            DISTANCE,
            listener
        )

        awaitClose {
            locationManager.removeUpdates(listener)
            currentListener = null
        }
    }

    override fun stopLocationUpdates() {
        currentListener?.let {
            locationManager.removeUpdates(it)
            currentListener = null
        }
    }

    private fun getBestProvider(): String? {
        return when {
            locationManager.isProviderEnabled(NativeLocationManager.GPS_PROVIDER) ->
                NativeLocationManager.GPS_PROVIDER

            locationManager.isProviderEnabled(NativeLocationManager.NETWORK_PROVIDER) ->
                NativeLocationManager.NETWORK_PROVIDER

            else -> null
        }
    }

    private fun NativeLocation.toLocation(): Location {
        return Location(
            latitude = latitude,
            longitude = longitude,
            accuracy = accuracy.toDouble(),
            altitude = if (hasAltitude()) altitude else null,
            timestamp = time
        )
    }
}
