package com.lebedaliv2601.base.platform.appInfo

interface ApplicationInfoManager {
    /**
     * Полная версия приложения. Примеры: 2.23.0-debug, 2.19.1-qa, 1.10.0
     */
    val versionFull: String

    /**
     * Версия приложения без суффиксов. Примеры: 2.23.0, 2.19.1, 1.10.0
     */
    val versionNumber: String

    /**
     * Флэйвор сборки
     */
    val mobileServices: MobileServices

    /**
     * Магазин, с которого установлено приложение
     */
    val source: AppStore

    /**
     * Дебажный билд
     */
    val isDebug: Boolean
}

enum class AppStore(val baseUrl: String) {
    GooglePlay("market://details?id="),
    AppGallery("appmarket://details?id="),
    RuStore("rustore://apps.rustore.ru/app/"),
    AppStore("itms-apps://itunes.apple.com/app/id")
}

enum class MobileServices { Google, Huawei, Apple }