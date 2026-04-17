package com.tastyhome.base.presentation.uiState

import com.tastyhome.base.presentation.error.UiError

sealed class UiState<T> {

    abstract val model: T?

    data class Loading<T>(override val model: T? = null) : UiState<T>()
    data class Error<T>(val error: UiError, override val model: T? = null) : UiState<T>()
    data class Success<T>(override val model: T) : UiState<T>()

}