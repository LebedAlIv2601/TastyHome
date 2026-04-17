package com.tastyhome.shared.root

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.experimental.stack.ChildStack
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.stackAnimation
import com.tastyhome.core.designSystem.theme.MyTheme
import com.tastyhome.core.navigation.animations.LocalAnimatedVisibilityScope
import com.tastyhome.core.navigation.animations.LocalSharedTransitionScope
import com.tastyhome.core.navigation.animations.defaultStackAnimator
import com.tastyhome.core.navigation.animations.predictiveBackParams
import com.tastyhome.core.themeManager.api.AppTheme

@OptIn(ExperimentalDecomposeApi::class)
@Composable
@Preview
fun Root(rootComponent: RootComponent) {
    var isThemeInitialized by remember { mutableStateOf(false) }
    var isDarkTheme by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        rootComponent.themeFlow.collect {
            if(!isThemeInitialized) isThemeInitialized = true
            isDarkTheme = isDarkTheme(it)
        }
    }

    if(isThemeInitialized) {
        MyTheme(isDarkTheme) {
            SharedTransitionLayout {
                Box(
                    modifier = Modifier
                        .systemBarsPadding()
                        .fillMaxSize()
                        .background(color = MyTheme.colors.bg)
                ) {
                    ChildStack(
                        modifier = Modifier.fillMaxSize(),
                        stack = rootComponent.childStack,
                        animation = stackAnimation(
                            selector = { child, otherChild, direction, isPredictiveBack -> defaultStackAnimator() },
                            predictiveBackParams = predictiveBackParams(
                                rootComponent.backHandler,
                                rootComponent::goBack
                            ),
                        )
                    ) { child ->
                        CompositionLocalProvider(
                            LocalSharedTransitionScope provides this@SharedTransitionLayout,
                            LocalAnimatedVisibilityScope provides this@ChildStack
                        ) {
                            child.instance.View(Modifier)
                        }
                    }
                }
            }
        }
    }
}

internal expect fun isDarkTheme(theme: AppTheme): Boolean

