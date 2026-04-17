package com.tastyhome.shared

import com.tastyhome.base.logger.L
import com.tastyhome.base.logger.LogSender
import com.tastyhome.core.themeManager.api.ThemeManager
import dev.zacsweers.metro.Inject

interface AppDelegate {
    fun initialize()
}

@Inject
class AppDelegateImpl(
    private val logSenders: Set<LogSender>
): AppDelegate {
    override fun initialize() {
        L.init(logSenders.toList())
    }
}