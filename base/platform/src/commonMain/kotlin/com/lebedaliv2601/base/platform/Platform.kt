package com.lebedaliv2601.base.platform

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

/**
 * Главный интерфейс для работы с платформой.
 *
 * Инкапсулирует всю платформенную логику:
 * - Android: работа с Context, ContentResolver, системными сервисами
 * - iOS: работа с UIKit, Foundation, платформенными API
 *
 * Инициализируется один раз при старте приложения.
 *
 * Android: требует Activity (MainActivity для Compose)
 * iOS: требует UIViewController (root controller)
 */
interface Platform {

    /**
     * Менеджер файловой системы.
     * Работает с файлами и изображениями.
     */
    val fileManager: FileManager

    /**
     * Пикер файлов и медиа.
     * Требует UI компонент (Activity/ViewController) для презентации.
     */
    val filePicker: FilePicker

    /**
     * Менеджер разрешений.
     * Запрос и проверка системных разрешений.
     */
    val permissionManager: PermissionManager

    /**
     * Менеджер локации.
     * Получение текущего местоположения.
     */
    val locationManager: LocationManager

    /**
     * Менеджер уведомлений.
     * Отправка локальных уведомлений.
     */
    val notificationManager: NotificationManager

    /**
     * Менеджер камеры.
     * Съемка фото.
     */
    val cameraManager: CameraManager

    /**
     * Менеджер вибрации и haptic feedback.
     * Вибрация и тактильные ощущения.
     */
    val vibrationManager: VibrationManager

    /**
     * Менеджер буфера обмена.
     */
    val clipboardManager: ClipboardManager

    /**
     * Вызов системных приложений и боттомщитов.
     */
    val systemNavigator: SystemNavigator

    /**
     * Получение информации о приложении (например, версии)
     */
    val appInfoManager: ApplicationInfoManager

    /**
     * Получение информации об устройстве (например, id или модель)
     */
    val deviceInfoManager: DeviceInfoManager
}