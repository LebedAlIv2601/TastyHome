package com.tastyhome.base.platform.vibration

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

@Suppress("detekt:MagicNumber")
internal class AndroidVibrationManager(context: Context) : VibrationManager {

    private val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    override fun vibrate(durationMillis: Long) {
        if (!vibrator.hasVibrator()) return

        val effect = VibrationEffect.createOneShot(durationMillis, VibrationEffect.DEFAULT_AMPLITUDE)
        vibrator.vibrate(effect)
    }

    override fun vibratePattern(pattern: LongArray) {
        if (!vibrator.hasVibrator() || pattern.isEmpty()) return

        val effect = VibrationEffect.createWaveform(pattern, -1)
        vibrator.vibrate(effect)
    }

    override fun haptic(type: HapticType) {
        if (!vibrator.hasVibrator()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val effectId = when (type) {
                HapticType.LIGHT -> VibrationEffect.EFFECT_CLICK
                HapticType.MEDIUM -> VibrationEffect.EFFECT_TICK
                HapticType.HEAVY -> VibrationEffect.EFFECT_HEAVY_CLICK
                HapticType.SUCCESS -> VibrationEffect.EFFECT_DOUBLE_CLICK
                HapticType.WARNING -> VibrationEffect.EFFECT_DOUBLE_CLICK
                HapticType.ERROR -> VibrationEffect.EFFECT_DOUBLE_CLICK
                HapticType.SELECTION -> VibrationEffect.EFFECT_TICK
            }
            val effect = VibrationEffect.createPredefined(effectId)
            vibrator.vibrate(effect)
        } else {
            val duration = when (type) {
                HapticType.LIGHT -> 10L
                HapticType.MEDIUM -> 20L
                HapticType.HEAVY -> 50L
                HapticType.SUCCESS -> 30L
                HapticType.WARNING -> 40L
                HapticType.ERROR -> 50L
                HapticType.SELECTION -> 10L
            }
            val effect = VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE)
            vibrator.vibrate(effect)
        }
    }

    override fun cancel() {
        vibrator.cancel()
    }
}
