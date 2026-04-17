package com.lebedaliv2601.base.platform.location

import com.lebedaliv2601.base.platform.permissions.Permission
import com.lebedaliv2601.base.platform.permissions.PermissionManager
import com.lebedaliv2601.base.platform.permissions.PermissionStatus
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLLocationAccuracyNearestTenMeters
import platform.Foundation.NSError
import platform.Foundation.timeIntervalSince1970
import platform.darwin.NSObject
import kotlin.coroutines.resume

internal class IosLocationManager(
    private val permissionManager: PermissionManager,
) : LocationManager {

    private val locationManager by lazy {
        CLLocationManager()
            .apply {
                setDesiredAccuracy(kCLLocationAccuracyNearestTenMeters)
            }
    }
    private var currentDelegate: NSObject? = null

    override suspend fun getCurrentLocation(): Location? {
        if (permissionManager.requestPermission(Permission.LOCATION) != PermissionStatus.GRANTED) {
            return null
        }
        return suspendCancellableCoroutine { continuation ->
            val singleDelegate = object : NSObject(), CLLocationManagerDelegateProtocol {
                override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
                    val clLocation = didUpdateLocations.lastOrNull() as? CLLocation
                    if (clLocation != null) {
                        continuation.resume(clLocation.toLocation())
                    } else {
                        continuation.resume(null)
                    }
                    currentDelegate = null
                }

                override fun locationManager(
                    manager: CLLocationManager,
                    didFailWithError: NSError
                ) {
                    continuation.resume(null)
                    currentDelegate = null
                }
            }

            currentDelegate = singleDelegate
            locationManager.setDelegate(singleDelegate)
            locationManager.requestLocation()

            continuation.invokeOnCancellation {
                locationManager.stopUpdatingLocation()
                locationManager.setDelegate(null)
                currentDelegate = null
            }
        }
    }

    override fun observeLocation(): Flow<Location> = callbackFlow {
        if (permissionManager.requestPermission(Permission.LOCATION) != PermissionStatus.GRANTED) {
            close()
            return@callbackFlow
        }
        val flowDelegate = object : NSObject(), CLLocationManagerDelegateProtocol {
            override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
                val clLocation = didUpdateLocations.lastOrNull() as? CLLocation
                if (clLocation != null) {
                    trySend(clLocation.toLocation())
                }
            }

            override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
                close(Exception(didFailWithError.localizedDescription))
            }
        }

        currentDelegate = flowDelegate
        locationManager.setDelegate(flowDelegate)
        locationManager.startUpdatingLocation()

        awaitClose {
            locationManager.stopUpdatingLocation()
            locationManager.setDelegate(null)
            currentDelegate = null
        }
    }

    override fun stopLocationUpdates() {
        locationManager.stopUpdatingLocation()
        locationManager.setDelegate(null)
        currentDelegate = null
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun CLLocation.toLocation(): Location {
        return Location(
            latitude = coordinate.useContents { latitude },
            longitude = coordinate.useContents { longitude },
            accuracy = horizontalAccuracy,
            altitude = if (verticalAccuracy >= 0) altitude else null,
            timestamp = (timestamp.timeIntervalSince1970 * 1000).toLong()
        )
    }
}
