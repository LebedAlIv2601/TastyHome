package com.lebedaliv2601.base.platform.permissions

/**
 * Менеджер для работы с разрешениями.
 *
 * Android: Activity Result API (RequestMultiplePermissions), проверка через ContextCompat.
 * iOS: специфичные для типа API (PHPhotoLibrary, CLLocationManager, AVCaptureDevice и т.д.).
 */
interface PermissionManager {
    
    /**
     * Проверить статус разрешения
     * 
     * @param permission Тип разрешения
     * @return Статус разрешения
     */
    suspend fun checkPermission(permission: Permission): PermissionStatus
    
    /**
     * Запросить разрешение у пользователя
     *
     * @param permission Тип разрешения
     * @return Статус после запроса
     */
    suspend fun requestPermission(permission: Permission): PermissionStatus
}
