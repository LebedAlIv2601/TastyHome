package com.lebedaliv2601.base.platform

import com.lebedaliv2601.base.platform.appInfo.ApplicationInfoManager
import platform.UIKit.UIViewController
import com.lebedaliv2601.base.platform.appInfo.IosAppFlavors
import com.lebedaliv2601.base.platform.appInfo.IosApplicationInfoManager
import com.lebedaliv2601.base.platform.camera.CameraManager
import com.lebedaliv2601.base.platform.camera.IosCameraManager
import com.lebedaliv2601.base.platform.clipboard.ClipboardManager
import com.lebedaliv2601.base.platform.clipboard.IosClipboardManager
import com.lebedaliv2601.base.platform.deviceInfo.DeviceInfoManager
import com.lebedaliv2601.base.platform.deviceInfo.IosDeviceInfoManager
import com.lebedaliv2601.base.platform.file.FileManager
import com.lebedaliv2601.base.platform.file.IosFileManager
import com.lebedaliv2601.base.platform.location.IosLocationManager
import com.lebedaliv2601.base.platform.location.LocationManager
import com.lebedaliv2601.base.platform.systemNavigator.IosSystemNavigator
import com.lebedaliv2601.base.platform.notification.IosNotificationManager
import com.lebedaliv2601.base.platform.notification.NotificationManager
import com.lebedaliv2601.base.platform.permissions.IosPermissionManager
import com.lebedaliv2601.base.platform.permissions.PermissionManager
import com.lebedaliv2601.base.platform.picker.FilePicker
import com.lebedaliv2601.base.platform.picker.IosFilePicker
import com.lebedaliv2601.base.platform.systemNavigator.SystemNavigator
import com.lebedaliv2601.base.platform.vibration.IosVibrationManager
import com.lebedaliv2601.base.platform.vibration.VibrationManager
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
