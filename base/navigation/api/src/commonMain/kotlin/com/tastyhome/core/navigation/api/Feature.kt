package com.tastyhome.core.navigation.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tastyhome.base.foundation.uri.Uri

interface Feature {
    @Composable
    fun View(modifier: Modifier)

    suspend fun onDeeplink(uri: Uri): Boolean = false
}

fun Feature(view: @Composable (Modifier) -> Unit): Feature {
    return object : Feature {
        @Composable
        override fun View(modifier: Modifier) {
            view(modifier)
        }
    }
}

interface DeeplinkableFeature : Feature {
    override suspend fun onDeeplink(uri: Uri): Boolean
}

fun DeeplinkableFeature(
    onDeeplink: suspend (Uri) -> Boolean,
    view: @Composable (Modifier) -> Unit,
): DeeplinkableFeature {
    return object : DeeplinkableFeature {
        @Composable
        override fun View(modifier: Modifier) {
            view(modifier)
        }

        override suspend fun onDeeplink(uri: Uri): Boolean {
            return onDeeplink(uri)
        }
    }
}