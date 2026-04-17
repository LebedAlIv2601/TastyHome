package com.lebedaliv2601.base.logger

interface LogSender {
    fun d(message: String?)
    fun d(message: String?, vararg extras: Any?)
    fun d(t: Throwable?, message: String?, vararg extras: Any?)
    fun d(t: Throwable?, vararg extras: Any?)

    fun i(message: String?)
    fun i(message: String?, vararg extras: Any?)
    fun i(t: Throwable?, message: String?, vararg extras: Any?)
    fun i(t: Throwable?, vararg extras: Any?)

    fun w(message: String?)
    fun w(message: String?, vararg extras: Any?)
    fun w(t: Throwable?, message: String?, vararg extras: Any?)
    fun w(t: Throwable?, vararg extras: Any?)

    fun e(message: String?)
    fun e(message: String?, vararg extras: Any?)
    fun e(t: Throwable?, message: String?, vararg extras: Any?)
    fun e(t: Throwable?, vararg extras: Any?)
}