package com.lebedaliv2601.base.network.httpClient.models.serialization

import io.ktor.client.plugins.contentnegotiation.ContentNegotiationConfig
import io.ktor.http.ContentType

interface SerializationStrategy {

    fun configure(plugin: ContentNegotiationConfig)

    fun contentType(): ContentType
}