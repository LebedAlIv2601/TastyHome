package com.lebedaliv2601.base.platform.location

import kotlinx.coroutines.flow.Flow

/**
 * Менеджер для работы с геолокацией.
 *
 * Android: android.location.LocationManager (getLastKnownLocation / getCurrentLocation / requestLocationUpdates).
 * iOS: CLLocationManager.
 */
interface LocationManager {
    
    /**
     * Получить текущее местоположение (разовый запрос)
     * 
     * @return Текущая локация или null если не удалось получить
     */
    suspend fun getCurrentLocation(): Location?
    
    /**
     * Подписаться на обновления местоположения
     * 
     * @return Flow с обновлениями локации
     */
    fun observeLocation(): Flow<Location>
    
    /**
     * Остановить отслеживание местоположения
     */
    fun stopLocationUpdates()
}
