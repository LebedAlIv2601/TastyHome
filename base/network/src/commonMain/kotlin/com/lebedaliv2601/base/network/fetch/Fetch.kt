package com.lebedaliv2601.base.network.fetch

import com.lebedaliv2601.base.domain.dataError.DataError
import com.lebedaliv2601.base.domain.dataError.asInternetConnectionError
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import kotlinx.io.IOException

suspend fun <T : Any> fetch(
    execute: suspend () -> T
): T {
    return try {
        execute()
    } catch (t: Throwable) {
        handleException(t)
    }
}

fun handleException(t: Throwable): Nothing {
    when (t) {
        is ResponseException -> throw DataError(
            code = t.response.status.value,
            message = t.message,
            cause = t
        )

        is HttpRequestTimeoutException, is ConnectTimeoutException, is SocketTimeoutException, is IOException ->
            throw t.asInternetConnectionError()

        else -> throw t
    }
}