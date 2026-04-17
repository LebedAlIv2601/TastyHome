package com.tastyhome.base.platform.location

import com.tastyhome.base.foundation.date.DateUtils

/**
 * Данные о геолокации
 */
data class Location(
    /**
     * Широта в градусах
     */
    val latitude: Double,
    
    /**
     * Долгота в градусах
     */
    val longitude: Double,
    
    /**
     * Точность в метрах
     */
    val accuracy: Double,
    
    /**
     * Высота над уровнем моря в метрах (опционально)
     */
    val altitude: Double? = null,
    
    /**
     * Время получения координат (timestamp в миллисекундах)
     */
    val timestamp: Long = DateUtils.currentTimeMillis()
)
