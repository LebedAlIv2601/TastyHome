package com.lebedaliv2601.base.domain.dataError

open class DataError(
    val code: Int,
    override val message: String?,
    override val cause: Throwable?
) : Throwable()