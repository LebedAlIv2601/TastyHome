package com.tastyhome.base.presentation.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

inline fun CoroutineScope.doWithLoading(
    loadingStateFlow: MutableStateFlow<Boolean>,
    crossinline block: suspend () -> Unit
) {
    if (loadingStateFlow.value) return

    loadingStateFlow.update { true }
    launch {
        block()
    }.invokeOnCompletion {
        loadingStateFlow.update { false }
    }
}