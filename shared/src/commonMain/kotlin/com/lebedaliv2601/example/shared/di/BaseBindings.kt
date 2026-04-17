package com.lebedaliv2601.example.shared.di

import com.lebedaliv2601.base.foundation.date.DateFormatter
import com.lebedaliv2601.base.foundation.date.platformDateFormatter
import com.lebedaliv2601.base.platform.Platform
import com.lebedaliv2601.base.platform.appInfo.ApplicationInfoManager
import com.lebedaliv2601.base.platform.camera.CameraManager
import com.lebedaliv2601.base.platform.clipboard.ClipboardManager
import com.lebedaliv2601.base.platform.deviceInfo.DeviceInfoManager
import com.lebedaliv2601.base.platform.file.FileManager
import com.lebedaliv2601.base.platform.location.LocationManager
import com.lebedaliv2601.base.platform.notification.NotificationManager
import com.lebedaliv2601.base.platform.permissions.PermissionManager
import com.lebedaliv2601.base.platform.picker.FilePicker
import com.lebedaliv2601.base.platform.systemNavigator.SystemNavigator
import com.lebedaliv2601.base.platform.vibration.VibrationManager
import com.lebedaliv2601.core.language.api.LanguageApi
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@BindingContainer
internal object BaseBindings {

    @Provides
    fun deviceInfoManager(platform: Platform): DeviceInfoManager {
        return platform.deviceInfoManager
    }

    @Provides
    fun appInfoManager(platform: Platform): ApplicationInfoManager {
        return platform.appInfoManager
    }

    @Provides
    fun fileManager(platform: Platform): FileManager {
        return platform.fileManager
    }

    @Provides
    fun filePicker(platform: Platform): FilePicker {
        return platform.filePicker
    }

    @Provides
    fun cameraManager(platform: Platform): CameraManager {
        return platform.cameraManager
    }

    @Provides
    fun clipboardManager(platform: Platform): ClipboardManager {
        return platform.clipboardManager
    }

    @Provides
    fun locationManager(platform: Platform): LocationManager {
        return platform.locationManager
    }

    @Provides
    fun notificationManager(platform: Platform): NotificationManager {
        return platform.notificationManager
    }

    @Provides
    fun permissionManager(platform: Platform): PermissionManager {
        return platform.permissionManager
    }

    @Provides
    fun systemNavigator(platform: Platform): SystemNavigator {
        return platform.systemNavigator
    }

    @Provides
    fun vibrationManager(platform: Platform): VibrationManager {
        return platform.vibrationManager
    }

    @SingleIn(AppScope::class)
    @Provides
    fun provideDateFormatter(languageApi: LanguageApi): DateFormatter {
        return platformDateFormatter(languageApi.currentLanguage().value)
    }
}