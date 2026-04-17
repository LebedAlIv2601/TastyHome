package com.tastyhome.base.domain.resource.holder

import com.tastyhome.base.domain.dataError.emptyDataError
import com.tastyhome.base.domain.resource.Resource
import com.tastyhome.base.domain.resource.holder.updateStrategies.DefaultResourceUpdateStrategy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class ResourceHolder<T>(
    private val cacheHolder: CacheHolder<T?>,
    private val strategy: ResourceUpdateStrategy = DefaultResourceUpdateStrategy.DataStoresOnError
) : CacheHolder<Resource<T>> {

    private val errorFlow = MutableStateFlow<Throwable?>(null)
    private val resFlow = MutableStateFlow<Resource<T>>(Resource.Error(emptyDataError()))

    override fun observe(): Flow<Resource<T>> = combine(
        cacheHolder.observe(),
        errorFlow
    ) { data, error ->
        when {
            error != null -> Resource.Error(error, data)
            data != null -> Resource.Success(data)
            else -> Resource.Error(emptyDataError())
        }
    }.onEach { resFlow.update { it } }

    override suspend fun update(res: Resource<T>) {
        val newValue = strategy.updateResource(resFlow.value, res)
        cacheHolder.update(res.value)
        errorFlow.update { newValue.throwableOrNull() }
    }
}