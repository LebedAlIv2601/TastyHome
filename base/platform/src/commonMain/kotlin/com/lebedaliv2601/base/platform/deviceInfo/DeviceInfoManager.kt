package com.lebedaliv2601.base.platform.deviceInfo

interface DeviceInfoManager {
    /**
     * Уникальный айди устройства
     */
    val deviceId: String

    /**
     * модель устройства, например: IPhone 17 Pro, Pixel 9
     */
    val model: String

    /**
     * операционная система: ios, android
     */
    val os: String

    /**
     * версия ОС устройства
     */
    val osVersion: String
}