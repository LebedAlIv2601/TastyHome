package com.tastyhome.base.localStorage.encrypted

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import platform.CoreCrypto.CCCrypt
import platform.CoreCrypto.kCCAlgorithmAES
import platform.CoreCrypto.kCCDecrypt
import platform.CoreCrypto.kCCEncrypt
import platform.CoreCrypto.kCCOptionPKCS7Padding
import platform.CoreCrypto.kCCSuccess
import platform.CoreFoundation.CFDictionaryRef
import platform.CoreFoundation.CFTypeRefVar
import platform.CoreFoundation.kCFBooleanTrue
import platform.Foundation.CFBridgingRelease
import platform.Foundation.NSData
import platform.Foundation.create
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecRandomCopyBytes
import platform.Security.errSecDuplicateItem
import platform.Security.errSecSuccess
import platform.Security.kSecAttrAccessible
import platform.Security.kSecAttrAccessibleAfterFirstUnlock
import platform.Security.kSecAttrAccount
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecRandomDefault
import platform.Security.kSecReturnData
import platform.Security.kSecValueData
import platform.posix.memcpy
import platform.posix.size_tVar

@OptIn(ExperimentalForeignApi::class)
internal actual object Crypto {
    private const val KEY_ALIAS = "com.tastyhome.datastore.key"
    private const val KEY_SIZE_BYTES = 32
    private const val IV_SIZE_BYTES = 16
    private const val AES_BLOCK_SIZE = 16

    private fun getOrCreateKey(): ByteArray {
        val existingKey = getKeyFromKeychain()
        if (existingKey != null) {
            return existingKey
        }

        val newKey = generateRandomKey()
        saveKeyToKeychain(newKey)
        return newKey
    }

    private fun generateRandomKey(): ByteArray {
        val key = ByteArray(KEY_SIZE_BYTES)
        key.usePinned { pinned ->
            SecRandomCopyBytes(kSecRandomDefault, KEY_SIZE_BYTES.toULong(), pinned.addressOf(0))
        }
        return key
    }

    private fun generateRandomIv(): ByteArray {
        val iv = ByteArray(IV_SIZE_BYTES)
        iv.usePinned { pinned ->
            SecRandomCopyBytes(kSecRandomDefault, IV_SIZE_BYTES.toULong(), pinned.addressOf(0))
        }
        return iv
    }

    private fun saveKeyToKeychain(key: ByteArray) {
        val keyData = key.toNSData()

        val query = mapOf<Any?, Any?>(
            kSecClass to kSecClassGenericPassword,
            kSecAttrAccount to KEY_ALIAS,
            kSecValueData to keyData,
            kSecAttrAccessible to kSecAttrAccessibleAfterFirstUnlock
        )

        val status = SecItemAdd(query as CFDictionaryRef, null)
        if (status != errSecSuccess && status != errSecDuplicateItem) {
            throw IllegalStateException("Failed to save key to Keychain: $status")
        }
    }

    private fun getKeyFromKeychain(): ByteArray? {
        val query = mapOf<Any?, Any?>(
            kSecClass to kSecClassGenericPassword,
            kSecAttrAccount to KEY_ALIAS,
            kSecReturnData to kCFBooleanTrue,
            kSecMatchLimit to kSecMatchLimitOne
        )

        memScoped {
            val result = alloc<CFTypeRefVar>()
            val status = SecItemCopyMatching(query as CFDictionaryRef, result.ptr)

            if (status == errSecSuccess) {
                val data = CFBridgingRelease(result.value) as? NSData
                return data?.toByteArray()
            }

            return null
        }
    }

    actual fun encrypt(data: ByteArray): ByteArray {
        val key = getOrCreateKey()
        val iv = generateRandomIv()
        val encrypted = encryptData(data, key, iv)
        return iv + encrypted
    }

    actual fun decrypt(data: ByteArray): ByteArray {
        if (data.size < IV_SIZE_BYTES) {
            throw IllegalStateException("Invalid encrypted data: too short")
        }

        val key = getOrCreateKey()
        val iv = data.copyOfRange(0, IV_SIZE_BYTES)
        val encrypted = data.copyOfRange(IV_SIZE_BYTES, data.size)

        return decryptData(encrypted, key, iv)
    }

    private fun encryptData(data: ByteArray, key: ByteArray, iv: ByteArray): ByteArray {
        val dataRef = data.toNSData()
        val keyData = key.toNSData()
        val ivData = iv.toNSData()

        return memScoped {
            val outputLength = alloc<size_tVar>()
            val bufferSize = data.size + AES_BLOCK_SIZE
            val buffer = allocArray<ByteVar>(bufferSize)

            val status = CCCrypt(
                kCCEncrypt.convert(),
                kCCAlgorithmAES.convert(),
                kCCOptionPKCS7Padding.convert(),
                keyData.bytes,
                key.size.convert(),
                ivData.bytes,
                dataRef.bytes,
                data.size.convert(),
                buffer,
                bufferSize.convert(),
                outputLength.ptr
            )

            if (status != kCCSuccess) {
                throw IllegalStateException("Encryption failed with status: $status")
            }

            buffer.readBytes(outputLength.value.toInt())
        }
    }

    private fun decryptData(data: ByteArray, key: ByteArray, iv: ByteArray): ByteArray {
        val dataRef = data.toNSData()
        val keyData = key.toNSData()
        val ivData = iv.toNSData()

        return memScoped {
            val outputLength = alloc<size_tVar>()
            val bufferSize = data.size + AES_BLOCK_SIZE
            val buffer = allocArray<ByteVar>(bufferSize)

            val status = CCCrypt(
                kCCDecrypt.convert(),
                kCCAlgorithmAES.convert(),
                kCCOptionPKCS7Padding.convert(),
                keyData.bytes,
                key.size.convert(),
                ivData.bytes,
                dataRef.bytes,
                data.size.convert(),
                buffer,
                bufferSize.convert(),
                outputLength.ptr
            )

            if (status != kCCSuccess) {
                throw IllegalStateException("Decryption failed with status: $status")
            }

            buffer.readBytes(outputLength.value.toInt())
        }
    }

    private fun ByteArray.toNSData(): NSData {
        return this.usePinned { pinned ->
            NSData.create(bytes = pinned.addressOf(0), length = this.size.toULong())
        }
    }

    private fun NSData.toByteArray(): ByteArray {
        return ByteArray(this.length.toInt()).apply {
            usePinned { pinned ->
                memcpy(pinned.addressOf(0), this@toByteArray.bytes, this@toByteArray.length)
            }
        }
    }
}