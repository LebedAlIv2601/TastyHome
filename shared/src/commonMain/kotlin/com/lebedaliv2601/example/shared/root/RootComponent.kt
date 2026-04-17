package com.lebedaliv2601.example.shared.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.lebedaliv2601.base.foundation.uri.Uri
import com.lebedaliv2601.core.navigation.BaseParentComponent
import com.lebedaliv2601.core.navigation.Router
import com.lebedaliv2601.core.themeManager.api.ThemeManager
import com.lebedaliv2601.example.shared.deeplink.DeeplinkBus
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

@AssistedInject
class RootComponent internal constructor(
    @Assisted componentContext: ComponentContext,
    private val deeplinkBus: DeeplinkBus,
    private val themeManager: ThemeManager
) : BaseParentComponent<Router, Config>(
    initialConfiguration = Config.Splash,
    serializer = Config.serializer(),
    router = Router { },
    componentContext = componentContext,
) {

    val themeFlow = themeManager.observeTheme()

    override fun createChild(config: Config, componentContext: ComponentContext): RootChild {
        return when (config) {
            Config.Splash -> RootChild.SplashChild()
        }
    }

    override fun onCreate() {
        super.onCreate()
        scope.launch {
            supervisorScope {
                deeplinkBus.deeplinkFlow
                    .onEach(::onDeeplink)
                    .launchIn(this)
            }
        }
    }

    override suspend fun handleDeeplink(uri: Uri): Boolean {
        val config = when {
            // deeplinks
            else -> null
        }
        config?.let(navigation::pushNew) ?: return false
        return true
    }

    fun newDeeplink(uri: Uri) {
        deeplinkBus.newDeeplink(uri)
    }

    fun goBack() {
        navigation.pop()
    }

    @AssistedFactory
    fun interface Factory {
        fun create(componentContext: ComponentContext): RootComponent
    }
}