package com.lebedaliv2601.base.platform.permissions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.lebedaliv2601.base.foundation.coroutines.MyDispatchers
import com.lebedaliv2601.base.platform.ActivityHolder
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

private const val PERMISSION_LAUNCHER_KEY = "permission_request"

internal class AndroidPermissionManager(
    private val activityHolder: ActivityHolder,
    private val context: Context,
) : PermissionManager, ActivityHolder.Callback {

    init {
        activityHolder.addCallback(this)
    }

    private var currentContinuation: Continuation<PermissionStatus>? = null
    private var permissionLauncher: ActivityResultLauncher<Array<String>>? = null

    override fun onChange(activity: Activity?) {
        permissionLauncher = null
        if (activity is ComponentActivity) {
            permissionLauncher = activity.activityResultRegistry.register(
                PERMISSION_LAUNCHER_KEY,
                activity,
                ActivityResultContracts.RequestMultiplePermissions()
            ) { result ->
                currentContinuation?.resume(resultToStatus(result))
                currentContinuation = null
            }
        }
    }

    override suspend fun checkPermission(permission: Permission): PermissionStatus {
        val permissions = permission.toAndroidPermissions()
        if (permissions.isEmpty()) return PermissionStatus.GRANTED
        val allGranted = permissions.any {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
        if (allGranted) return PermissionStatus.GRANTED
        val activity = activityHolder.activity
        val anyDeniedForever = permissions.any {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED &&
                (activity == null || !ActivityCompat.shouldShowRequestPermissionRationale(activity, it))
        }
        return if (anyDeniedForever) PermissionStatus.DENIED_FOREVER else PermissionStatus.DENIED
    }

    override suspend fun requestPermission(permission: Permission): PermissionStatus {
        val permissions = permission.toAndroidPermissions()
        if (permissions.isEmpty()) return PermissionStatus.GRANTED
        if (
            permissions.any {
                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }
        ) return PermissionStatus.GRANTED
        val launcher = permissionLauncher ?: return checkPermission(permission)
        return withContext(MyDispatchers.MainImmediate) {
            suspendCancellableCoroutine { cont ->
                currentContinuation = cont
                launcher.launch(permissions.toTypedArray())
            }
        }
    }

    private fun resultToStatus(result: Map<String, Boolean>): PermissionStatus {
        val anyGranted = result.values.any { it }
        if (anyGranted) return PermissionStatus.GRANTED
        val activity = activityHolder.activity
        val anyPermanentDenial =
            result.entries.any {
                !it.value && (
                    activity == null ||
                        !ActivityCompat.shouldShowRequestPermissionRationale(activity, it.key)
                    )
            }
        return if (anyPermanentDenial) PermissionStatus.DENIED_FOREVER else PermissionStatus.DENIED
    }
}

private fun Permission.toAndroidPermissions(): List<String> = when (this) {
    Permission.CAMERA -> listOf(Manifest.permission.CAMERA)
    Permission.LOCATION -> listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    Permission.STORAGE, Permission.MEDIA -> when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> listOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
        )

        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> listOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO
        )

        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        else -> listOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    Permission.MICROPHONE -> listOf(Manifest.permission.RECORD_AUDIO)
    Permission.NOTIFICATION -> when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> listOf(Manifest.permission.POST_NOTIFICATIONS)
        else -> emptyList()
    }
}
