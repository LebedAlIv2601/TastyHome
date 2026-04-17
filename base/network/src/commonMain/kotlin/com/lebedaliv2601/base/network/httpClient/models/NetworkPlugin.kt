package com.lebedaliv2601.base.network.httpClient.models

import io.ktor.client.plugins.api.ClientPlugin

interface NetworkPlugin<T : Any> {
    val plugin: ClientPlugin<T>
}