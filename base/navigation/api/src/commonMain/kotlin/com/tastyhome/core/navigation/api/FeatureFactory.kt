package com.tastyhome.core.navigation.api

import com.arkivanov.decompose.ComponentContext
import com.tastyhome.base.foundation.uri.Uri

interface Args
interface Callbacks

object NoCallbacks : Callbacks
fun interface BackOnlyCallback : Callbacks { fun goBack() }
object EmptyArgs : Args

interface FeatureFactory<ARGS : Args, CALLBACKS : Callbacks> {
    fun create(componentContext: ComponentContext, args: ARGS, callbacks: CALLBACKS): Feature
}

interface DeeplinkableFeatureFactory<ARGS : Args, CALLBACKS : Callbacks> : FeatureFactory<ARGS, CALLBACKS> {
    override fun create(componentContext: ComponentContext, args: ARGS, callbacks: CALLBACKS): DeeplinkableFeature
    suspend fun canHandle(deeplink: Uri): Boolean
    suspend fun parseArgs(deeplink: Uri): ARGS
}