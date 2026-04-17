package com.tastyhome.base.platform.systemNavigator

import com.tastyhome.base.platform.file.image.Image

/**
 * Открытие системных приложений и экранов.
 *
 * Android: Intent с флагами, пакеты системных приложений.
 * iOS: UIApplication.openURL / openSettings URL scheme.
 *
 */
interface SystemNavigator {

    /**
     * Открыть настройки.
     * Android: общие настройки устройства (Settings.ACTION_SETTINGS).
     * iOS: настройки приложения (публичного API для общих настроек устройства нет).
     */
    fun openSettings()

    fun openAppSettings()

    fun openMarket(packageNameOrAppId: String, onFailure: (Throwable) -> Unit = {})

    fun openPhoneDialer(phoneNumber: String, onFailure: (Throwable) -> Unit = {})

    fun openBrowser(url: String, onFailure: (Throwable) -> Unit = {})

    suspend fun share(text: String, title: String? = null, onFailure: (Throwable) -> Unit = {})

    suspend fun shareImage(image: Image, title: String? = null, onFailure: (Throwable) -> Unit = {})

    suspend fun shareFile(byteArray: ByteArray, type: String, name: String, onFailure: (Throwable) -> Unit = {})
}
