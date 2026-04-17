package com.tastyhome.base.platform.appInfo

import platform.Foundation.NSBundle

data class IosAppFlavors(
    val isDebug: Boolean
)

internal class IosApplicationInfoManager(private val flavors: IosAppFlavors) : ApplicationInfoManager {

    private val appVersionName: String = NSBundle.mainBundle
        .objectForInfoDictionaryKey("CFBundleShortVersionString") as? String
        ?: ""

    override val versionFull: String = appVersionName

    override val versionNumber: String = versionFull
        .substringBefore("-")

    override val mobileServices: MobileServices = MobileServices.Apple

    override val source: AppStore = AppStore.AppStore

    override val isDebug: Boolean = flavors.isDebug
}
