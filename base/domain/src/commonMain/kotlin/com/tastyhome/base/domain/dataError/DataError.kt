package com.tastyhome.base.domain.dataError

open class DataError(
    val code: Int,
    override val message: String?,
    override val cause: Throwable?
) : Throwable()