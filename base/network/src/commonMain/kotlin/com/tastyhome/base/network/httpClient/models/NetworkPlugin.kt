package com.tastyhome.base.network.httpClient.models

import io.ktor.client.plugins.api.ClientPlugin

interface NetworkPlugin<T : Any> {
    val plugin: ClientPlugin<T>
}