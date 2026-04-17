package com.lebedaliv2601.core.navigation.animations

import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.PredictiveBackParams
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.StackAnimator
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.essenty.backhandler.BackHandler

@OptIn(ExperimentalDecomposeApi::class)
expect fun defaultStackAnimator(): StackAnimator

@OptIn(ExperimentalDecomposeApi::class)
expect fun <C : Any, T : Any> predictiveBackParams(
    backHandler: BackHandler,
    onBack: () -> Unit,
): (ChildStack<C, T>) -> PredictiveBackParams?
