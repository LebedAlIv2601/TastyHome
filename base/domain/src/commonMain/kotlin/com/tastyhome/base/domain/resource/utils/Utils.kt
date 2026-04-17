package com.tastyhome.base.domain.resource.utils

import com.tastyhome.base.domain.dataError.emptyDataError
import com.tastyhome.base.domain.resource.Resource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun <T, R> Flow<Resource<T>>.mapResource(block: (T) -> R): Flow<Resource<R>> {
    return this.map { it.map(block) }
}

inline fun <T> runCatchingResource(block: () -> T?): Resource<T> {
    return try {
        block()?.let { Resource.Success(it) }
            ?: Resource.Error(emptyDataError())
    } catch (t: CancellationException) {
        throw t
    } catch (t: Throwable) {
        Resource.Error(t)
    }
}