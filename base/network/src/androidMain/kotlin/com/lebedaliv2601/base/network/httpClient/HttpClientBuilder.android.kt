package com.lebedaliv2601.base.network.httpClient

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.android.Android

internal actual fun provideBaseHttpClient(
    block: HttpClientConfig<*>.() -> Unit
): HttpClient {
    return HttpClient(Android, block)
}