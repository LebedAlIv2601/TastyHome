package com.tastyhome.core.designSystem.theme.colors

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class MyColors(
    val bg: Color,
    val primary: Color,
    val stroke: Color
)

internal fun lightColors() = MyColors(
    bg = lime100,
    primary = lime500,
    stroke = black
)

internal fun darkColors() = MyColors(
    bg = lime100,
    primary = lime500,
    stroke = black
)

private val lime100 = Color(0xFFCAEB97)
private val lime500 = Color(0xFF84CC18)
private val black = Color(0xFF000000)