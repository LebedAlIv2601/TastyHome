package com.tastyhome.base.platform

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import com.tastyhome.base.platform.appInfo.AndroidAppFlavors
import com.tastyhome.base.platform.appInfo.AndroidApplicationInfoManager
import com.tastyhome.base.platform.appInfo.ApplicationInfoManager
import com.tastyhome.base.platform.camera.AndroidCameraManager
import com.tastyhome.base.platform.camera.CameraManager
import com.tastyhome.base.platform.clipboard.AndroidClipboardManager
import com.tastyhome.base.platform.clipboard.ClipboardManager
import com.tastyhome.base.platform.deviceInfo.AndroidDeviceInfoManager
import com.tastyhome.base.platform.deviceInfo.DeviceInfoManager
import com.tastyhome.base.platform.file.AndroidFileManager
import com.tastyhome.base.platform.file.FileManager
import com.tastyhome.base.platform.location.AndroidLocationManager
import com.tastyhome.base.platform.location.LocationManager
import com.tastyhome.base.platform.notification.AndroidNotificationManager
import com.tastyhome.base.platform.notification.NotificationManager
import com.tastyhome.base.platform.permissions.AndroidPermissionManager
import com.tastyhome.base.platform.permissions.PermissionManager
import com.tastyhome.base.platform.picker.AndroidFilePicker
import com.tastyhome.base.platform.picker.FilePicker
import com.tastyhome.base.platform.systemNavigator.AndroidSystemNavigator
import com.tastyhome.base.platform.systemNavigator.SystemNavigator
import com.tastyhome.base.platform.vibration.AndroidVibrationManager
import com.tastyhome.base.platform.vibration.VibrationManager

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
