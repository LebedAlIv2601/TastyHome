package com.lebedaliv2601.base.network.httpClient.models

private const val CONNECT_TIMEOUT_DEFAULT = 5000L
private const val REQUEST_TIMEOUT_DEFAULT = 120000L
private const val SOCKET_TIMEOUT_DEFAULT = 40000L

data class Timeouts(
    val connectMillis: Long = CONNECT_TIMEOUT_DEFAULT,
    val requestMillis: Long = REQUEST_TIMEOUT_DEFAULT,
    val socketMillis: Long = SOCKET_TIMEOUT_DEFAULT
)