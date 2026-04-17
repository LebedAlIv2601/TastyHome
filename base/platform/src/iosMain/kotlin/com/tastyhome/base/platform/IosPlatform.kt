package com.tastyhome.base.platform

import com.tastyhome.base.platform.appInfo.ApplicationInfoManager
import platform.UIKit.UIViewController
import com.tastyhome.base.platform.appInfo.IosAppFlavors
import com.tastyhome.base.platform.appInfo.IosApplicationInfoManager
import com.tastyhome.base.platform.camera.CameraManager
import com.tastyhome.base.platform.camera.IosCameraManager
import com.tastyhome.base.platform.clipboard.ClipboardManager
import com.tastyhome.base.platform.clipboard.IosClipboardManager
import com.tastyhome.base.platform.deviceInfo.DeviceInfoManager
import com.tastyhome.base.platform.deviceInfo.IosDeviceInfoManager
import com.tastyhome.base.platform.file.FileManager
import com.tastyhome.base.platform.file.IosFileManager
import com.tastyhome.base.platform.location.IosLocationManager
import com.tastyhome.base.platform.location.LocationManager
import com.tastyhome.base.platform.systemNavigator.IosSystemNavigator
import com.tastyhome.base.platform.notification.IosNotificationManager
import com.tastyhome.base.platform.notification.NotificationManager
import com.tastyhome.base.platform.permissions.IosPermissionManager
import com.tastyhome.base.platform.permissions.PermissionManager
import com.tastyhome.base.platform.picker.FilePicker
import com.tastyhome.base.platform.picker.IosFilePicker
import com.tastyhome.base.platform.systemNavigator.SystemNavigator
import com.tastyhome.base.platform.vibration.IosVibrationManager
import com.tastyhome.base.platform.vibration.VibrationManager
import kotlin.experimental.ExperimentalNativeApi

@OptIn(ExperimentalNativeApi::class)
class IosPlatform(
    private val viewController: UIViewController,
    private val flavors: IosAppFlavors
) : Platform {

    override val fileManager: FileManager by lazy { IosFileManager() }
    override val permissionManager: PermissionManager by lazy { IosPermissionManager() }
    override val filePicker: FilePicker by lazy { IosFilePicker(viewController, permissionManager) }
    override val locationManager: LocationManager by lazy { IosLocationManager(permissionManager) }
    override val cameraManager: CameraManager by lazy {
        IosCameraManager(viewController, permissionManager, fileManager)
    }
    override val vibrationManager: VibrationManager by lazy { IosVibrationManager() }
    override val clipboardManager: ClipboardManager by lazy { IosClipboardManager() }
    override val systemNavigator: SystemNavigator by lazy {
        IosSystemNavigator(viewController, fileManager, appInfoManager)
    }
    override val notificationManager: NotificationManager by lazy {
        IosNotificationManager(permissionManager)
    }
    override val appInfoManager: ApplicationInfoManager by lazy {
        IosApplicationInfoManager(flavors)
    }
    override val deviceInfoManager: DeviceInfoManager by lazy {
        IosDeviceInfoManager()
    }
}
