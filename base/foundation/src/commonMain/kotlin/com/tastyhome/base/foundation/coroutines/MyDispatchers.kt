package com.tastyhome.base.foundation.coroutines

import kotlinx.coroutines.CoroutineDispatcher

object MyDispatchers {
    val Default: CoroutineDispatcher
        get() = MyDispatchersPlugin.default.create()

    val IO: CoroutineDispatcher
        get() = MyDispatchersPlugin.io.create()

    val Main: CoroutineDispatcher
        get() = MyDispatchersPlugin.main.create()

    val MainImmediate: CoroutineDispatcher
        get() = MyDispatchersPlugin.mainImmediate.create()
}