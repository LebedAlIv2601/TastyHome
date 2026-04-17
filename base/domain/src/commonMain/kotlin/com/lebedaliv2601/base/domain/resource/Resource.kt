package com.lebedaliv2601.base.domain.resource

sealed class Resource<out T> {

    abstract val value: T?

    data class Error<T>(val error: Throwable, override val value: T? = null) : Resource<T>()

    data class Success<T>(override val value: T) : Resource<T>()

    fun getOrNull(): T? {
        return value
    }

    fun throwableOrNull(): Throwable? {
        return when (this) {
            is Error -> error
            is Success -> null
        }
    }

    fun getOrThrow(): T {
        return when (this) {
            is Error -> throw error
            is Success -> value
        }
    }

    fun getOrDefault(default: @UnsafeVariance T): T {
        return value ?: default
    }

    fun onSuccess(block: (T) -> Unit) {
        when (this) {
            is Error -> {}
            is Success -> block(value)
        }
    }

    fun onError(block: (Throwable, T?) -> Unit) {
        when (this) {
            is Error -> block(error, value)
            is Success -> {}
        }
    }

    fun <R> map(block: (T) -> R): Resource<R> {
        return when (this) {
            is Error -> Error(error, value?.let { block(it) })
            is Success -> Success(block(value))
        }
    }
}