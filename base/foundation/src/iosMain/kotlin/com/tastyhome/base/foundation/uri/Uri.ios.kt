package com.tastyhome.base.foundation.uri

import platform.Foundation.NSURL

actual class Uri(private val nsUrl: NSURL) {
    actual val scheme: String? get() = nsUrl.scheme
    actual val host: String? get() = nsUrl.host
    actual val authority: String? get() = nsUrl.host?.let { h ->
        nsUrl.port?.let { p -> "$h:$p" } ?: h
    }
    actual val path: String? get() = nsUrl.path
    actual val query: String? get() = nsUrl.query
    actual val fragment: String? get() = nsUrl.fragment
    actual val port: Int get() = (nsUrl.port?.intValue) ?: -1
    actual fun getQueryParameter(name: String): String? = parseQuery().firstOrNull { it.first == name }?.second
    actual fun getPathSegments(): List<String> = nsUrl.path?.split('/')?.filter { it.isNotEmpty() }
        ?: emptyList()
    actual fun builder(): UriBuilder = UriBuilder().apply {
        scheme(nsUrl.scheme)
        authority(nsUrl.host?.let { h -> nsUrl.port?.let { p -> "$h:$p" } ?: h })
        path(nsUrl.path)
        query(nsUrl.query)
        fragment(nsUrl.fragment)
    }
    actual override fun toString(): String = nsUrl.absoluteString ?: ""

    private fun parseQuery(): List<Pair<String, String>> {
        val q = nsUrl.query ?: return emptyList()
        return q.split('&').mapNotNull { param ->
            val idx = param.indexOf('=')
            if (idx < 0) null else Pair(
                param.substring(0, idx).decodeUriComponent(),
                param.substring(idx + 1).decodeUriComponent()
            )
        }
    }
}

private fun String.decodeUriComponent(): String =
    replace("+", " ").decodeURLEncoded()

private const val HEX_RADIX = 16
private const val HEX_PREFIX_LENGTH = 2

private fun String.decodeURLEncoded(): String {
    val result = StringBuilder()
    var i = 0
    while (i < length) {
        val c = this[i]
        when (c) {
            '%' -> {
                if (i + HEX_PREFIX_LENGTH < length) {
                    val hex = substring(i + 1, i + 3)
                    result.append(hex.toInt(HEX_RADIX).toChar())
                    i += HEX_PREFIX_LENGTH
                } else {
                    result.append(c)
                }
            }
            else -> result.append(c)
        }
        i++
    }
    return result.toString()
}

actual class UriBuilder(
    private var scheme: String? = null,
    private var authority: String? = null,
    private var path: String? = null,
    private var pathSegments: MutableList<String> = mutableListOf(),
    private var query: String? = null,
    private var fragment: String? = null
) {
    actual fun scheme(scheme: String?): UriBuilder {
        this.scheme = scheme
        return this
    }
    actual fun authority(authority: String?): UriBuilder {
        this.authority = authority
        return this
    }
    actual fun path(path: String?): UriBuilder {
        this.path = path
        this.pathSegments.clear()
        path?.split('/')?.filter { it.isNotEmpty() }?.let { pathSegments.addAll(it) }
        return this
    }
    actual fun appendPath(segment: String): UriBuilder {
        pathSegments.add(segment)
        return this
    }
    actual fun query(query: String?): UriBuilder {
        this.query = query
        return this
    }
    actual fun fragment(fragment: String?): UriBuilder {
        this.fragment = fragment
        return this
    }
    actual fun build(): Uri {
        val pathStr = when {
            pathSegments.isNotEmpty() -> "/" + pathSegments.joinToString("/")
            !path.isNullOrEmpty() -> if (path!!.startsWith("/")) path else "/$path"
            else -> ""
        }
        val sb = StringBuilder()
        scheme?.let { sb.append(it).append(":") }
        if (authority != null) sb.append("//").append(authority)
        sb.append(pathStr)
        query?.let { sb.append("?").append(it) }
        fragment?.let { sb.append("#").append(it) }
        val url = NSURL.URLWithString(sb.toString()) ?: NSURL.URLWithString("")!!
        return Uri(url)
    }
}

actual fun parseUri(uriString: String): Uri? {
    if (uriString.isBlank()) return null
    val url = NSURL.URLWithString(uriString) ?: return null
    return Uri(url)
}

actual fun platformUriBuilder(): UriBuilder = UriBuilder()