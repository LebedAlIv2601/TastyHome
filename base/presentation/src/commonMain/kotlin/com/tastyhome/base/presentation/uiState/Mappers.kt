package com.tastyhome.base.presentation.uiState

import com.tastyhome.base.domain.resource.Resource
import com.tastyhome.base.foundation.coroutines.MyDispatchers
import com.tastyhome.base.presentation.error.UiError
import com.tastyhome.base.presentation.error.toUiError
import kotlinx.coroutines.withContext

suspend fun <Domain, Model> Resource<Domain>.toUiState(
    errorMapper: (Throwable) -> UiError = { it.toUiError() },
    isLoading: Boolean,
    mapper: suspend (Domain) -> Model
): UiState<Model> {
    return withContext(MyDispatchers.Default) {
        if (isLoading) {
            UiState.Loading(this@toUiState.value?.let { mapper(it) })
        } else {
            when (val res = this@toUiState) {
                is Resource.Error -> UiState.Error(errorMapper(res.error), res.value?.let { mapper(it) })
                is Resource.Success<Domain> -> UiState.Success(mapper(res.value))
            }
        }
    }
}