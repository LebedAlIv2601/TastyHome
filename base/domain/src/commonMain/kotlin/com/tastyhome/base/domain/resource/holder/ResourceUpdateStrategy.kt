package com.tastyhome.base.domain.resource.holder

import com.tastyhome.base.domain.resource.Resource

interface ResourceUpdateStrategy {
    fun <T> updateResource(old: Resource<T>, new: Resource<T>): Resource<T>
}