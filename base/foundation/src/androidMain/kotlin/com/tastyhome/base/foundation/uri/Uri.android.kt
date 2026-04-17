package com.tastyhome.base.foundation.uri

import android.net.Uri as AndroidUri

actual class Uri(private val androidUri: AndroidUri) {
    actual val scheme: String? get() = androidUri.scheme
    actual val host: String? get() = androidUri.host
    actual val authority: String? get() = androidUri.authority
    actual val path: String? get() = androidUri.path
    actual val query: String? get() = androidUri.query
    actual val fragment: String? get() = androidUri.fragment
    actual val port: Int get() = androidUri.port
    actual fun getQueryParameter(name: String): String? = androidUri.getQueryParameter(name)
    actual fun getPathSegments(): List<String> = androidUri.pathSegments
    actual fun builder(): UriBuilder = UriBuilder(androidUri.buildUpon())
    actual override fun toString(): String = androidUri.toString()
}

actual class UriBuilder(private val builder: AndroidUri.Builder) {
    actual fun scheme(scheme: String?): UriBuilder {
        builder.scheme(scheme)
        return this
    }
    actual fun authority(authority: String?): UriBuilder {
        builder.authority(authority)
        return this
    }
    actual fun path(path: String?): UriBuilder {
        builder.path(path)
        return this
    }
    actual fun appendPath(segment: String): UriBuilder {
        builder.appendPath(segment)
        return this
    }
    actual fun query(query: String?): UriBuilder {
        builder.query(query)
        return this
    }
    actual fun fragment(fragment: String?): UriBuilder {
        builder.fragment(fragment)
        return this
    }
    actual fun build(): Uri = Uri(builder.build())
}

actual fun parseUri(uriString: String): Uri? {
    if (uriString.isBlank()) return null
    return try {
        Uri(AndroidUri.parse(uriString))
    } catch (_: Exception) {
        null
    }
}

actual fun platformUriBuilder(): UriBuilder = UriBuilder(AndroidUri.Builder())