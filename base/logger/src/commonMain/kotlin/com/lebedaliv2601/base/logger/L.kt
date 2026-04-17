package com.lebedaliv2601.base.logger

object L {
    private var senders: List<LogSender> = emptyList()

    fun init(senders: List<LogSender>) {
        this.senders = senders
    }

    fun d(message: String?) {
        senders.forEach { it.d(message) }
    }

    fun d(message: String?, vararg extras: Any?) {
        senders.forEach { it.d(message, *extras) }
    }

    fun d(t: Throwable?, message: String?, vararg extras: Any?) {
        senders.forEach { it.d(t, message, *extras) }
    }

    fun d(t: Throwable?, vararg extras: Any?) {
        senders.forEach { it.d(t, *extras) }
    }

    fun i(message: String?) {
        senders.forEach { it.i(message) }
    }

    fun i(message: String?, vararg extras: Any?) {
        senders.forEach { it.i(message, *extras) }
    }

    fun i(t: Throwable?, message: String?, vararg extras: Any?) {
        senders.forEach { it.i(t, message, *extras) }
    }

    fun i(t: Throwable?, vararg extras: Any?) {
        senders.forEach { it.i(t, *extras) }
    }

    fun w(message: String?) {
        senders.forEach { it.w(message) }
    }

    fun w(message: String?, vararg extras: Any?) {
        senders.forEach { it.w(message, *extras) }
    }

    fun w(t: Throwable?, message: String?, vararg extras: Any?) {
        senders.forEach { it.w(t, message, *extras) }
    }

    fun w(t: Throwable?, vararg extras: Any?) {
        senders.forEach { it.w(t, *extras) }
    }

    fun e(message: String?) {
        senders.forEach { it.e(message) }
    }

    fun e(message: String?, vararg extras: Any?) {
        senders.forEach { it.e(message, *extras) }
    }

    fun e(t: Throwable?, message: String?, vararg extras: Any?) {
        senders.forEach { it.e(t, message, *extras) }
    }

    fun e(t: Throwable?, vararg extras: Any?) {
        senders.forEach { it.e(t, *extras) }
    }
}