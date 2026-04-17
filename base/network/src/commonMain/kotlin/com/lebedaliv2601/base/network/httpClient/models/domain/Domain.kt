package com.lebedaliv2601.base.network.httpClient.models.domain

interface Domain {
    val key: String
    fun provideUrl(env: NetworkEnvironment): String
}