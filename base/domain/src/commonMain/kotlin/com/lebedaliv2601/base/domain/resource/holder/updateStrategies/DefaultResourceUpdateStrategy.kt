package com.lebedaliv2601.base.domain.resource.holder.updateStrategies

import com.lebedaliv2601.base.domain.resource.Resource
import com.lebedaliv2601.base.domain.resource.holder.ResourceUpdateStrategy

sealed interface DefaultResourceUpdateStrategy : ResourceUpdateStrategy {

    object Straight : ResourceUpdateStrategy {
        override fun <T> updateResource(old: Resource<T>, new: Resource<T>): Resource<T> {
            return new
        }
    }

    object DataStoresOnError : DefaultResourceUpdateStrategy{
        override fun <T> updateResource(old: Resource<T>, new: Resource<T>): Resource<T> {
            return when(new) {
                is Resource.Error<T> -> {
                    val oldValue = old.value
                    if(oldValue != null && new.value == null) {
                        new.map { oldValue }
                    } else {
                        new
                    }
                }
                is Resource.Success<T> -> new
            }
        }
    }
}
