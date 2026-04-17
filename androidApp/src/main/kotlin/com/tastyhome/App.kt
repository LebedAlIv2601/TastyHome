package com.tastyhome

import android.app.Application
import android.content.Context
import com.tastyhome.base.logger.L
import com.tastyhome.base.platform.ActivityHolder
import com.tastyhome.base.platform.AndroidPlatform
import com.tastyhome.base.platform.appInfo.AndroidAppFlavors
import com.tastyhome.base.platform.appInfo.AppStore
import com.tastyhome.base.platform.appInfo.MobileServices
import com.tastyhome.core.network.baseClient.domain.MyNetworkEnvironment
import com.tastyhome.shared.di.AndroidAppGraph
import com.tastyhome.shared.di.createRootGraph
import com.tastyhome.shared.language.updateContextWithActualLanguage

class App : Application() {

    internal val activityHolder: ActivityHolder = ActivityHolder()

    val appGraph: AndroidAppGraph by lazy {
        createRootGraph(
            this,
            AndroidPlatform(
                activityHolder = activityHolder,
                context = this,
                appIcon = R.drawable.ic_launcher_foreground,
                androidAppFlavors = AndroidAppFlavors(
                    mobileServices = MobileServices.Google,
                    source = AppStore.GooglePlay,
                    isDebug = BuildConfig.DEBUG
                )
            ),
            MyNetworkEnvironment.Prod,
        )
    }

    override fun attachBaseContext(base: Context?) {
        val newBase = base?.let { updateContextWithActualLanguage(base) } ?: base
        super.attachBaseContext(newBase)
    }

    override fun onCreate() {
        super.onCreate()
        appGraph.appDelegate.initialize()

        L.i("Start App")
    }
}

val Context.appGraph: AndroidAppGraph
    get() = when(this) {
        is App -> appGraph
        else -> this.applicationContext.appGraph
    }
