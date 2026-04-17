package com.lebedaliv2601.base.presentation.error

import com.lebedaliv2601.base.domain.dataError.DataError
import com.lebedaliv2601.base.domain.dataError.EMPTY_DATA_ERROR_CODE
import com.lebedaliv2601.base.domain.dataError.INTERNET_CONNECTION_ERROR_CODE

fun Throwable.toUiError(): UiError {
    return when (this) {
        is DataError -> this.toUiError()
        else -> UnknownUiError()
    }
}

fun DataError.toUiError(): UiError {
    return when (code) {
        INTERNET_CONNECTION_ERROR_CODE -> NoInternetUiError()
        EMPTY_DATA_ERROR_CODE -> EmptyDataUiError()
        else -> UnknownUiError()
    }
}