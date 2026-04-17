package com.lebedaliv2601.base.domain.resource.holder

import com.lebedaliv2601.base.domain.resource.Resource

interface ResourceUpdateStrategy {
    fun <T> updateResource(old: Resource<T>, new: Resource<T>): Resource<T>
}