@file:OptIn(ExperimentalDecomposeApi::class)

package com.tastyhome.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.PredictiveBackGestureOverlay
import com.arkivanov.essenty.backhandler.BackDispatcher
import com.tastyhome.shared.root.Root
import com.tastyhome.shared.root.RootComponent
import platform.UIKit.UIViewController

fun rootController(
    rootComponent: RootComponent,
    backDispatcher: BackDispatcher,
): UIViewController = ComposeUIViewController {
    PredictiveBackGestureOverlay(
        backDispatcher = backDispatcher,
        backIcon = { progress, _ ->
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = progress)),
            )
        },
        modifier = Modifier.fillMaxSize(),
    ) {
        Root(rootComponent)
    }
}