package com.tastyhome.core.designSystem.theme.typography

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp

@Immutable
data class MyTypography(
    val h1: TextStyle,
    val body: TextStyle,
    val caption: TextStyle
)

internal fun typography() = MyTypography(
    h1 = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 28.sp
    ),
    body = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 18.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 12.sp
    )
)