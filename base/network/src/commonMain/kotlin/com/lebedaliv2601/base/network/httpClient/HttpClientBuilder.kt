package com.lebedaliv2601.base.network.httpClient

import com.lebedaliv2601.base.network.httpClient.models.domain.NetworkEnvironment
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.CookiesStorage

typealias HttpClientSetting = HttpClientConfig<*>.() -> Unit

interface HttpClientBuilder {

    val environment: NetworkEnvironment
    val settings: MutableList<HttpClientSetting>

    fun addSetting(setting: HttpClientSetting) {
        settings.add(setting)
    }

    fun build(): HttpClient {
        return provideBaseHttpClient { settings.forEach { it() } }
    }
}

internal expect fun provideBaseHttpClient(block: HttpClientConfig<*>.() -> Unit = {}): HttpClient