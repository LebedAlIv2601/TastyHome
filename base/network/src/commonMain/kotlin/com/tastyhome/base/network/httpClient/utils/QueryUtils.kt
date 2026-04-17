package com.tastyhome.base.network.httpClient.utils

import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse

suspend inline fun <reified T> HttpClient.post(
    urlString: String,
    body: T,
    block: HttpRequestBuilder.() -> Unit = {}
): HttpResponse {
    return this.post(urlString = urlString) {
        setBody(body)
        block()
    }
}