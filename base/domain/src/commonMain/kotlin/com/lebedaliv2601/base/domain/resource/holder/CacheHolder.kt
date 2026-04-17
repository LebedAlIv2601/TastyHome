package com.lebedaliv2601.base.domain.resource.holder

import kotlinx.coroutines.flow.Flow

interface CacheHolder<T> {
    fun observe(): Flow<T?>
    suspend fun update(data: T)
}

fun <T> CacheHolder(
    observe: () -> Flow<T?>,
    update: suspend (T) -> Unit
): CacheHolder<T> {
    return object : CacheHolder<T> {
        override fun observe(): Flow<T?> {
            return observe.invoke()
        }

        override suspend fun update(data: T) {
            update.invoke(data)
        }
    }
}