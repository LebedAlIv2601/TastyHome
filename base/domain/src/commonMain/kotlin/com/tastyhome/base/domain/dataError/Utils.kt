package com.tastyhome.base.domain.dataError

const val INTERNET_CONNECTION_ERROR_CODE = 1001
const val EMPTY_DATA_ERROR_CODE = 1002

fun Throwable.asInternetConnectionError(): DataError {
    return DataError(
        code = INTERNET_CONNECTION_ERROR_CODE,
        message = message,
        cause = cause
    )
}

fun emptyDataError(): DataError {
    return DataError(
        code = EMPTY_DATA_ERROR_CODE,
        message = null,
        cause = null
    )
}

fun Throwable.isEmptyDataError(): Boolean {
    return (this as? DataError)?.code == EMPTY_DATA_ERROR_CODE
}