package com.lebedaliv2601.base.foundation.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

object MyDispatchersPlugin {

    var default = DispatchersFactory(Dispatchers::Default)
    var io = DispatchersFactory(Dispatchers::IO)
    var main = DispatchersFactory(Dispatchers::Main)
    var mainImmediate = DispatchersFactory { Dispatchers.Main.immediate }

    fun interface DispatchersFactory {
        fun create(): CoroutineDispatcher
    }
}