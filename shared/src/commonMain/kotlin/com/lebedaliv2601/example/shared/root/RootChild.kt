package com.lebedaliv2601.example.shared.root

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.lebedaliv2601.core.designSystem.theme.MyTheme
import com.lebedaliv2601.core.navigation.api.Feature

import kotlinx.serialization.Serializable

sealed interface RootChild : Feature {
    class SplashChild() : RootChild, Feature by object : Feature {
        @Composable
        override fun View(modifier: Modifier) {
            Box(modifier.fillMaxSize().background(MyTheme.colors.bg))
        }
    }
}

@Serializable
sealed class Config {

    @Serializable
    data object Splash : Config()
}