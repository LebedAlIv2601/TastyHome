package com.tastyhome.base.domain.resource.holder.updateStrategies

import com.tastyhome.base.domain.resource.Resource
import com.tastyhome.base.domain.resource.holder.ResourceUpdateStrategy

sealed interface DefaultResourceUpdateStrategy : ResourceUpdateStrategy {

    object Straight : DefaultResourceUpdateStrategy {
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
                        Resource.Error(new.error, oldValue)
                    } else {
                        new
                    }
                }
                is Resource.Success<T> -> new
            }
        }
    }
}
