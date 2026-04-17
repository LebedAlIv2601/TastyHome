package com.lebedaliv2601.base.network.httpClient.models.serialization

import io.ktor.client.plugins.contentnegotiation.ContentNegotiationConfig
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class JsonSerializationStrategy(
    private val json: Json = defaultJson
) : SerializationStrategy {

    override fun configure(plugin: ContentNegotiationConfig) {
        plugin.json(json)
    }

    override fun contentType(): ContentType = ContentType.Application.Json

    companion object {
        val defaultJson = Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        }
    }
}