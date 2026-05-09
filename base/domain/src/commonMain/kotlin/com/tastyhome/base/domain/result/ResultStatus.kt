package com.tastyhome.base.domain.result

sealed interface ResultStatus {
    data object Success : ResultStatus
    data class Error(val error: Throwable) : ResultStatus
}