package com.lebedaliv2601.base.platform

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import com.lebedaliv2601.base.platform.appInfo.AndroidAppFlavors
import com.lebedaliv2601.base.platform.appInfo.AndroidApplicationInfoManager
import com.lebedaliv2601.base.platform.appInfo.ApplicationInfoManager
import com.lebedaliv2601.base.platform.camera.AndroidCameraManager
import com.lebedaliv2601.base.platform.camera.CameraManager
import com.lebedaliv2601.base.platform.clipboard.AndroidClipboardManager
import com.lebedaliv2601.base.platform.clipboard.ClipboardManager
import com.lebedaliv2601.base.platform.deviceInfo.AndroidDeviceInfoManager
import com.lebedaliv2601.base.platform.deviceInfo.DeviceInfoManager
import com.lebedaliv2601.base.platform.file.AndroidFileManager
import com.lebedaliv2601.base.platform.file.FileManager
import com.lebedaliv2601.base.platform.location.AndroidLocationManager
import com.lebedaliv2601.base.platform.location.LocationManager
import com.lebedaliv2601.base.platform.notification.AndroidNotificationManager
import com.lebedaliv2601.base.platform.notification.NotificationManager
import com.lebedaliv2601.base.platform.permissions.AndroidPermissionManager
import com.lebedaliv2601.base.platform.permissions.PermissionManager
import com.lebedaliv2601.base.platform.picker.AndroidFilePicker
import com.lebedaliv2601.base.platform.picker.FilePicker
import com.lebedaliv2601.base.platform.systemNavigator.AndroidSystemNavigator
import com.lebedaliv2601.base.platform.systemNavigator.SystemNavigator
import com.lebedaliv2601.base.platform.vibration.AndroidVibrationManager
import com.lebedaliv2601.base.platform.vibration.VibrationManager

class ActivityHolder {
    var activity: Activity? = null
        set(value) {
            field = value
            callbacks.forEach { it.onChange(value) }
        }

    fun interface Callback {
        fun onChange(activity: Activity?)
    }

    private val callbacks = mutableListOf<Callback>()

    fun addCallback(callback: Callback) {
        callbacks.add(callback)
        callback.onChange(activity)
    }
}

class AndroidPlatform(
    private val activityHolder: ActivityHolder,
    private val context: Context,
    private val androidAppFlavors: AndroidAppFlavors,
    appIcon: Int,
) : Platform {

    override val fileManager: FileManager by lazy {
        AndroidFileManager(context).also { manager ->
            (context as? Application)?.registerActivityLifecycleCallbacks(
                object : ActivityLifecycleCallbacks {
                    override fun onActivityDestroyed(p0: Activity) {
                        manager.clearTempFiles()
                    }
                }
            )
        }
    }

    override val permissionManager: PermissionManager = AndroidPermissionManager(activityHolder, context)
    override val filePicker: FilePicker = AndroidFilePicker(activityHolder, context, permissionManager, fileManager)
    override val cameraManager: CameraManager =
        AndroidCameraManager(activityHolder, context, permissionManager, fileManager)

    override val locationManager: LocationManager by lazy { AndroidLocationManager(context, permissionManager) }
    override val vibrationManager: VibrationManager by lazy { AndroidVibrationManager(context) }
    override val clipboardManager: ClipboardManager by lazy { AndroidClipboardManager(context) }
    override val systemNavigator: SystemNavigator by lazy {
        AndroidSystemNavigator(activityHolder, fileManager, appInfoManager)
    }
    override val notificationManager: NotificationManager by lazy {
        AndroidNotificationManager(activityHolder, context, permissionManager, appIcon)
    }
    override val appInfoManager: ApplicationInfoManager by lazy {
        AndroidApplicationInfoManager(context, androidAppFlavors)
    }
    override val deviceInfoManager: DeviceInfoManager by lazy {
        AndroidDeviceInfoManager(context)
    }
}

private interface ActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {
    override fun onActivityCreated(p0: Activity, p1: Bundle?) {}
    override fun onActivityDestroyed(p0: Activity) {}
    override fun onActivityPaused(p0: Activity) {}
    override fun onActivityResumed(p0: Activity) {}
    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {}
    override fun onActivityStarted(p0: Activity) {}
    override fun onActivityStopped(p0: Activity) {}
}
