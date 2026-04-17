package com.tastyhome.base.platform.vibration

interface VibrationManager {
    
    fun vibrate(durationMillis: Long = 100)
    
    fun vibratePattern(pattern: LongArray)
    
    fun haptic(type: HapticType)
    
    fun cancel()
}
