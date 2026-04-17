package com.lebedaliv2601.base.network.httpClient

import com.lebedaliv2601.base.network.httpClient.models.NetworkPlugin
import com.lebedaliv2601.base.network.httpClient.models.Timeouts
import com.lebedaliv2601.base.network.httpClient.models.domain.Domain
import com.lebedaliv2601.base.network.httpClient.models.serialization.SerializationStrategy
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.contentType

fun <T : HttpClientBuilder> T.setting(setting: HttpClientSetting): T {
    return this.apply { addSetting(setting) }
}

fun <T : HttpClientBuilder> T.timeouts(timeouts: Timeouts): T {
    return apply {
        addSetting {
            installOrReplace(HttpTimeout) {
                connectTimeoutMillis = timeouts.connectMillis
                requestTimeoutMillis = timeouts.requestMillis
                socketTimeoutMillis = timeouts.socketMillis
            }
        }
    }
}

fun <T : HttpClientBuilder> T.serializationStrategy(serializationStrategy: SerializationStrategy): T {
    return apply {
        addSetting {
            installOrReplace(ContentNegotiation) {
                serializationStrategy.configure(this)
            }
            defaultRequest {
                contentType(serializationStrategy.contentType())
            }
        }
    }
}

fun <T : HttpClientBuilder> T.baseUrl(domain: Domain): T {
    return apply {
        addSetting {
            defaultRequest {
                url(domain.provideUrl(environment))
            }
        }
    }
}

fun <P : Any, T : HttpClientBuilder> T.installPlugin(
    plugin: NetworkPlugin<P>,
    configure: P.() -> Unit = {}
): T {
    return apply {
        addSetting {
            install(plugin.plugin, configure)
        }
    }
}