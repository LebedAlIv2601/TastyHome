package com.lebedaliv2601.base.platform.vibration

import com.lebedaliv2601.base.foundation.coroutines.MyDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import platform.AudioToolbox.AudioServicesPlaySystemSound
import platform.AudioToolbox.kSystemSoundID_Vibrate
import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle
import platform.UIKit.UINotificationFeedbackGenerator
import platform.UIKit.UINotificationFeedbackType
import platform.UIKit.UISelectionFeedbackGenerator

internal class IosVibrationManager : VibrationManager {

    private var patternJob: Job? = null
    private val scope = CoroutineScope(MyDispatchers.Main)

    private val lightImpactGenerator by lazy {
        UIImpactFeedbackGenerator(UIImpactFeedbackStyle.UIImpactFeedbackStyleLight).apply { prepare() }
    }
    private val mediumImpactGenerator by lazy {
        UIImpactFeedbackGenerator(UIImpactFeedbackStyle.UIImpactFeedbackStyleMedium).apply { prepare() }
    }
    private val heavyImpactGenerator by lazy {
        UIImpactFeedbackGenerator(UIImpactFeedbackStyle.UIImpactFeedbackStyleHeavy).apply { prepare() }
    }
    private val notificationGenerator by lazy {
        UINotificationFeedbackGenerator().apply { prepare() }
    }
    private val selectionGenerator by lazy { UISelectionFeedbackGenerator().apply { prepare() } }

    override fun vibrate(durationMillis: Long) {
        AudioServicesPlaySystemSound(kSystemSoundID_Vibrate)
    }

    override fun vibratePattern(pattern: LongArray) {
        patternJob?.cancel()

        if (pattern.isEmpty()) return

        patternJob = scope.launch {
            var isVibrating = false
            for (duration in pattern) {
                if (isVibrating) {
                    AudioServicesPlaySystemSound(kSystemSoundID_Vibrate)
                }
                delay(duration)
                isVibrating = !isVibrating
            }
        }
    }

    override fun haptic(type: HapticType) {
        when (type) {
            HapticType.LIGHT -> {
                lightImpactGenerator.impactOccurred()
            }

            HapticType.MEDIUM -> {
                mediumImpactGenerator.impactOccurred()
            }

            HapticType.HEAVY -> {
                heavyImpactGenerator.impactOccurred()
            }

            HapticType.SUCCESS -> {
                notificationGenerator.notificationOccurred(UINotificationFeedbackType.UINotificationFeedbackTypeSuccess)
            }

            HapticType.WARNING -> {
                notificationGenerator.notificationOccurred(UINotificationFeedbackType.UINotificationFeedbackTypeWarning)
            }

            HapticType.ERROR -> {
                notificationGenerator.notificationOccurred(UINotificationFeedbackType.UINotificationFeedbackTypeError)
            }

            HapticType.SELECTION -> {
                selectionGenerator.selectionChanged()
            }
        }
    }

    override fun cancel() {
        patternJob?.cancel()
        patternJob = null
    }
}
