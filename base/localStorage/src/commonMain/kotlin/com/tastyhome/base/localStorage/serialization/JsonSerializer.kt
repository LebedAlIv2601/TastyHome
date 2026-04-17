package com.tastyhome.base.localStorage.serialization

import androidx.datastore.core.okio.OkioSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import okio.BufferedSink
import okio.BufferedSource
import okio.IOException

class JsonSerializer<T>(
    private val serializer: KSerializer<T>,
    private val default: T,
    private val json: Json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
) : OkioSerializer<T> {

    override val defaultValue: T
        get() = default

    override suspend fun readFrom(source: BufferedSource): T {
        return try {
            val jsonString = source.readUtf8()
            json.decodeFromString(serializer, jsonString)
        } catch (e: Exception) {
            throw IOException("Error deserializing data", e)
        }
    }

    override suspend fun writeTo(t: T, sink: BufferedSink) {
        try {
            val jsonString = json.encodeToString(serializer, t)
            sink.writeUtf8(jsonString)
        } catch (e: Exception) {
            throw IOException("Error serializing data", e)
        }
    }
}

fun <T> KSerializer<T>.toDataStoreSerializer(default: T): OkioSerializer<T> = JsonSerializer(this, default)