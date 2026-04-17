package com.tastyhome.shared.di

import com.tastyhome.base.foundation.date.DateFormatter
import com.tastyhome.base.foundation.date.platformDateFormatter
import com.tastyhome.base.platform.Platform
import com.tastyhome.base.platform.appInfo.ApplicationInfoManager
import com.tastyhome.base.platform.camera.CameraManager
import com.tastyhome.base.platform.clipboard.ClipboardManager
import com.tastyhome.base.platform.deviceInfo.DeviceInfoManager
import com.tastyhome.base.platform.file.FileManager
import com.tastyhome.base.platform.location.LocationManager
import com.tastyhome.base.platform.notification.NotificationManager
import com.tastyhome.base.platform.permissions.PermissionManager
import com.tastyhome.base.platform.picker.FilePicker
import com.tastyhome.base.platform.systemNavigator.SystemNavigator
import com.tastyhome.base.platform.vibration.VibrationManager
import com.tastyhome.core.language.api.LanguageApi
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