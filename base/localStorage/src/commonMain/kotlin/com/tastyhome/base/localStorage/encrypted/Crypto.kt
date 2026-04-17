package com.tastyhome.base.localStorage.encrypted

internal expect object Crypto {
    fun encrypt(data: ByteArray): ByteArray
    fun decrypt(data: ByteArray): ByteArray
}
