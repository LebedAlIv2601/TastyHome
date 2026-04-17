package com.lebedaliv2601.base.domain.resource.holder

import com.lebedaliv2601.base.domain.resource.Resource
import com.lebedaliv2601.base.domain.resource.holder.updateStrategies.DefaultResourceUpdateStrategy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class LocalResourceHolder<T>(
    private val strategy: ResourceUpdateStrategy = DefaultResourceUpdateStrategy.Straight
) : CacheHolder<Resource<T>> {

    private val dataFlow: MutableStateFlow<T?> = MutableStateFlow(null)
    private val resourceHolder: ResourceHolder<T> = ResourceHolder(
        CacheHolder(
            observe = { dataFlow },
            update = { data -> dataFlow.update { data } }
        ),
        strategy = strategy
    )

    override fun observe(): Flow<Resource<T>> = resourceHolder.observe()

    override suspend fun update(res: Resource<T>) = resourceHolder.update(res)
}