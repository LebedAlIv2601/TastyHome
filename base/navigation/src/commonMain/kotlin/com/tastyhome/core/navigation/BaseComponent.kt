package com.tastyhome.core.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.active
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.arkivanov.essenty.lifecycle.subscribe
import com.tastyhome.base.foundation.coroutines.MyDispatchers
import com.tastyhome.base.foundation.uri.Uri
import com.tastyhome.core.navigation.api.Feature
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.KSerializer
import kotlin.coroutines.CoroutineContext

fun interface Router {
    fun goBack()
}

abstract class BaseComponent<T : Router>(
    protected val router: T,
    componentContext: ComponentContext,
    coroutineContext: CoroutineContext = MyDispatchers.MainImmediate,
) : ComponentContext by componentContext {

    protected val scope: CoroutineScope = coroutineScope(coroutineContext + SupervisorJob())

    init {
        lifecycle.subscribe(
            ::onCreate,
            ::onStart,
            ::onResume,
            ::onPause,
            ::onStop,
            ::onDestroy,
        )
    }

    protected open fun onCreate() {}
    protected open fun onDestroy() {}
    protected open fun onStart() {}
    protected open fun onStop() {}
    protected open fun onResume() {}
    protected open fun onPause() {}

    companion object {
        const val DEBOUNCE_DELAY = 300L
        const val REFRESH_DELAY = 600L
    }
}

abstract class BaseParentComponent<T : Router, C : Any>(
    private val serializer: KSerializer<C>,
    initialStack: List<C>,
    router: T,
    componentContext: ComponentContext,
    coroutineContext: CoroutineContext = MyDispatchers.MainImmediate,
) : BaseComponent<T>(router, componentContext, coroutineContext) {

    constructor(
        serializer: KSerializer<C>,
        initialConfiguration: C,
        router: T,
        componentContext: ComponentContext,
        coroutineContext: CoroutineContext = MyDispatchers.MainImmediate,
    ) : this(serializer, listOf(initialConfiguration), router, componentContext, coroutineContext)

    protected val navigation = StackNavigation<C>()

    val childStack: Value<ChildStack<*, Feature>> by lazy {
        childStack(
            source = navigation,
            serializer = serializer,
            initialStack = { initialStack },
            handleBackButton = true,
            childFactory = ::createChild,
        )
    }

    protected abstract fun createChild(config: C, componentContext: ComponentContext): Feature

    suspend fun onDeeplink(uri: Uri): Boolean {
        if (childStack.active.instance.onDeeplink(uri)) return true
        return if (handleDeeplink(uri)) {
            childStack.active.instance.onDeeplink(uri)
            true
        } else {
            false
        }
    }

    protected abstract suspend fun handleDeeplink(uri: Uri): Boolean
}