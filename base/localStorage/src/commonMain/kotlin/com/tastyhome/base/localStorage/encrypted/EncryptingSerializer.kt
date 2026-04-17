package com.tastyhome.base.localStorage.encrypted

import androidx.datastore.core.okio.OkioSerializer
import com.tastyhome.base.foundation.coroutines.MyDispatchers
import kotlinx.coroutines.withContext
import okio.Buffer
import okio.BufferedSink
import okio.BufferedSource

internal class EncryptingSerializer<T>(
    private val delegate: OkioSerializer<T>,
    private val associatedData: ByteArray,
) : OkioSerializer<T> {

    override val defaultValue: T
        get() = delegate.defaultValue

    override suspend fun readFrom(source: BufferedSource): T {
        return withContext(MyDispatchers.IO) {
            try {
                val encryptedData = source.readByteArray()
                val decryptedData = Crypto.decrypt(encryptedData)
                
                if (decryptedData.size < associatedData.size) {
                    throw IllegalStateException("Decrypted data too short")
                }
                
                val readAssociatedData = decryptedData.copyOfRange(0, associatedData.size)
                if (!readAssociatedData.contentEquals(associatedData)) {
                    throw IllegalStateException("Associated data mismatch - possible data corruption or tampering")
                }
                
                val actualData = decryptedData.copyOfRange(associatedData.size, decryptedData.size)
                val buffer = Buffer().apply { write(actualData) }
                
                delegate.readFrom(buffer)
            } catch (e: Exception) {
                throw IllegalStateException("Failed to decrypt data: ${e.message}", e)
            }
        }
    }

    override suspend fun writeTo(t: T, sink: BufferedSink) {
        withContext(MyDispatchers.IO) {
            try {
                val buffer = Buffer()
                delegate.writeTo(t, buffer)
                val plainData = buffer.readByteArray()
                
                val dataWithAssociated = associatedData + plainData
                val encryptedData = Crypto.encrypt(dataWithAssociated)
                
                sink.write(encryptedData)
            } catch (e: Exception) {
                throw IllegalStateException("Failed to encrypt data: ${e.message}", e)
            }
        }
    }
}

internal fun <T> OkioSerializer<T>.encrypted(
    associatedData: ByteArray = byteArrayOf(),
): EncryptingSerializer<T> = EncryptingSerializer(this, associatedData)
