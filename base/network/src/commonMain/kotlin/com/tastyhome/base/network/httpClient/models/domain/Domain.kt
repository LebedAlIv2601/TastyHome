package com.tastyhome.base.network.httpClient.models.domain

interface Domain {
    val key: String
    fun provideUrl(env: NetworkEnvironment): String
}