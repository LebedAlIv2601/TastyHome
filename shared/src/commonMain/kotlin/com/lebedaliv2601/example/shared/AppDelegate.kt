package com.lebedaliv2601.example.shared

import com.lebedaliv2601.base.logger.L
import com.lebedaliv2601.base.logger.LogSender
import com.lebedaliv2601.core.themeManager.api.ThemeManager
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