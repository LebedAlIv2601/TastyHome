package com.tastyhome.base.foundation.uri

expect class Uri {
    val scheme: String?
    val host: String?
    val authority: String?
    val path: String?
    val query: String?
    val fragment: String?
    val port: Int
    fun getQueryParameter(name: String): String?
    fun getPathSegments(): List<String>
    fun builder(): UriBuilder
    override fun toString(): String
}

expect class UriBuilder {
    fun scheme(scheme: String?): UriBuilder
    fun authority(authority: String?): UriBuilder
    fun path(path: String?): UriBuilder
    fun appendPath(segment: String): UriBuilder
    fun query(query: String?): UriBuilder
    fun fragment(fragment: String?): UriBuilder
    fun build(): Uri
}

expect fun parseUri(uriString: String): Uri?

fun uriBuilder(): UriBuilder = platformUriBuilder()
expect fun platformUriBuilder(): UriBuilder