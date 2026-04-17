package com.tastyhome.shared.logger

import co.touchlab.kermit.Logger
import com.tastyhome.base.logger.LogSender
import dev.zacsweers.metro.Inject

@Inject
internal class KermitLogSender : LogSender {

    init {
        Logger.setTag("Log")
    }

    override fun d(message: String?) {
        Logger.d(messageString = message.orEmpty())
    }

    override fun d(message: String?, vararg extras: Any?) {
        Logger.d(
            messageString = formatMessageWithArgs(message.orEmpty(), *extras)
        )
    }

    override fun d(t: Throwable?, message: String?, vararg extras: Any?) {
        Logger.d(
            messageString = formatMessageWithArgs(
                message.orEmpty() + t?.stackTraceToString(),
                *extras
            )
        )
    }

    override fun d(t: Throwable?, vararg extras: Any?) {
        Logger.d(
            messageString = formatMessageWithArgs(
                t?.stackTraceToString(),
                *extras
            )
        )
    }

    override fun i(message: String?) {
        Logger.i(messageString = message.orEmpty())
    }

    override fun i(message: String?, vararg extras: Any?) {
        Logger.i(
            messageString = formatMessageWithArgs(message.orEmpty(), *extras)
        )
    }

    override fun i(t: Throwable?, message: String?, vararg extras: Any?) {
        Logger.i(
            messageString = formatMessageWithArgs(
                message.orEmpty() + t?.stackTraceToString(),
                *extras
            )
        )
    }

    override fun i(t: Throwable?, vararg extras: Any?) {
        Logger.i(
            messageString = formatMessageWithArgs(
                t?.stackTraceToString(),
                *extras
            )
        )
    }

    override fun w(message: String?) {
        Logger.w(messageString = message.orEmpty())
    }

    override fun w(message: String?, vararg extras: Any?) {
        Logger.w(
            messageString = formatMessageWithArgs(message.orEmpty(), *extras)
        )
    }

    override fun w(t: Throwable?, message: String?, vararg extras: Any?) {
        Logger.w(
            messageString = formatMessageWithArgs(
                message.orEmpty() + t?.stackTraceToString(),
                *extras
            )
        )
    }

    override fun w(t: Throwable?, vararg extras: Any?) {
        Logger.w(
            messageString = formatMessageWithArgs(
                t?.stackTraceToString(),
                *extras
            )
        )
    }

    override fun e(message: String?) {
        Logger.e(messageString = message.orEmpty())
    }

    override fun e(message: String?, vararg extras: Any?) {
        Logger.e(
            messageString = formatMessageWithArgs(message.orEmpty(), *extras)
        )
    }

    override fun e(t: Throwable?, message: String?, vararg extras: Any?) {
        Logger.e(
            messageString = formatMessageWithArgs(
                message.orEmpty() + t?.stackTraceToString(),
                *extras
            )
        )
    }

    override fun e(t: Throwable?, vararg extras: Any?) {
        Logger.e(
            messageString = formatMessageWithArgs(
                t?.stackTraceToString(),
                *extras
            )
        )
    }

    private fun formatMessageWithArgs(message: String?, vararg args: Any?): String {
        return message.orEmpty() + "\n" + args.joinToString("\n")
    }
}