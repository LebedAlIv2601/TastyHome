package com.lebedaliv2601.base.presentation.uiState

import com.lebedaliv2601.base.presentation.error.UiError

sealed class UiState<T> {

    abstract val model: T?

    data class Loading<T>(override val model: T? = null) : UiState<T>()
    data class Error<T>(val error: UiError, override val model: T? = null) : UiState<T>()
    data class Success<T>(override val model: T) : UiState<T>()

}