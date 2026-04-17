package com.tastyhome.base.platform.appInfo

import android.content.Context

data class AndroidAppFlavors(
    val mobileServices: MobileServices,
    val source: AppStore,
    val isDebug: Boolean
)

internal class AndroidApplicationInfoManager(
    context: Context,
    flavors: AndroidAppFlavors
) : ApplicationInfoManager {

    override val versionFull: String = context
        .packageManager
        .getPackageInfo(context.packageName, 0)
        .versionName
        .orEmpty()

    override val versionNumber: String = versionFull
        .substringBefore("-")

    override val mobileServices = flavors.mobileServices

    override val source = flavors.source

    override val isDebug = flavors.isDebug
}
