package com.tastyhome.base.presentation.uiState

import com.tastyhome.base.presentation.error.UiError
import com.tastyhome.base.presentation.uiState.UiState.Error
import com.tastyhome.base.presentation.uiState.UiState.Loading
import com.tastyhome.base.presentation.uiState.UiState.Success
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

fun <T> Flow<UiState<T>>.onLoading(block: (T?) -> Unit): Flow<UiState<T>> {
    return this.onEach { uiState ->
        when (uiState) {
            is Loading -> block(uiState.model)
            else -> {}
        }
    }
}

fun <T> Flow<UiState<T>>.onSuccess(block: (T) -> Unit): Flow<UiState<T>> {
    return this.onEach { uiState ->
        when (uiState) {
            is Success -> block(uiState.model)
            else -> {}
        }
    }
}

fun <T> Flow<UiState<T>>.onError(block: (UiError, T?) -> Unit): Flow<UiState<T>> {
    return this.onEach { uiState ->
        when (uiState) {
            is Error -> block(uiState.error, uiState.model)
            else -> {}
        }
    }
}

fun <T> UiState<T>.onLoading(block: (T?) -> Unit) {
    when (this) {
        is Loading -> block(model)
        else -> {}
    }
}

fun <T> UiState<T>.onSuccess(block: (T) -> Unit) {
    when (this) {
        is Success -> block(model)
        else -> {}
    }
}

fun <T> UiState<T>.onError(block: (UiError, T?) -> Unit) {
    when (this) {
        is Error -> block(error, model)
        else -> {}
    }
}

fun <T, R> UiState<T>.map(block: (T) -> R): UiState<R> {
    return when(this) {
        is Loading -> Loading(model?.let { block(it) })
        is Error -> Error(error, model?.let { block(it) })
        is Success -> Success(block(model))
    }
}