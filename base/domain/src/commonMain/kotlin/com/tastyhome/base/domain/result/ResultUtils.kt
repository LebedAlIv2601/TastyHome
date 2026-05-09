package com.tastyhome.base.domain.result

import com.tastyhome.base.domain.dataError.emptyDataError
import kotlinx.coroutines.CancellationException

inline fun <T> runForResult(block: () -> T?): Result<T> {
    return runCatching {
        block().takeIf { data ->
            (data as? Iterable<*>)?.iterator()?.hasNext() ?: true
        } ?: throw emptyDataError()
    }.onFailure {
        if (it is CancellationException) throw it
    }
}

fun <T> Result<T>.status(): ResultStatus {
    val exception = exceptionOrNull()
    return when {
        exception != null && isFailure -> ResultStatus.Error(exception)
        else -> ResultStatus.Success
    }
}
