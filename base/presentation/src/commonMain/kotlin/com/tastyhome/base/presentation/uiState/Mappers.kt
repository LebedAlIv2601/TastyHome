package com.tastyhome.base.presentation.uiState

import com.tastyhome.base.domain.dataError.emptyDataError
import com.tastyhome.base.domain.result.ResultStatus
import com.tastyhome.base.foundation.coroutines.MyDispatchers
import com.tastyhome.base.presentation.error.UiError
import com.tastyhome.base.presentation.error.toUiError
import kotlinx.coroutines.withContext

suspend fun <Domain, Model> createUiState(
    data: Domain?,
    isLoading: Boolean,
    status: ResultStatus?,
    errorMapper: (Throwable) -> UiError = { it.toUiError() },
    mapper: suspend (Domain) -> Model
): UiState<Model> {
    return withContext(MyDispatchers.Default) {
        if (isLoading || (data == null && status == ResultStatus.Success)) {
            UiState.Loading(data?.let { mapper(it) })
        } else {
            when (status) {
                is ResultStatus.Error -> UiState.Error(errorMapper(status.error), data?.let { mapper(it) })
                is ResultStatus.Success -> UiState.Success(mapper(data!!))
                null -> UiState.Error(errorMapper(emptyDataError()), data?.let { mapper(it) })
            }
        }
    }
}