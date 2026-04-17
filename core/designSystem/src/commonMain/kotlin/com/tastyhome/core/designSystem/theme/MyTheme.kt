package com.tastyhome.core.designSystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import com.tastyhome.core.designSystem.theme.colors.MyColors
import com.tastyhome.core.designSystem.theme.colors.darkColors
import com.tastyhome.core.designSystem.theme.colors.lightColors
import com.tastyhome.core.designSystem.theme.typography.MyTypography
import com.tastyhome.core.designSystem.theme.typography.typography

internal val LocalColors = staticCompositionLocalOf { lightColors() }
internal val LocalTypography = staticCompositionLocalOf { typography() }
internal val LocalTheme = staticCompositionLocalOf { false }

object MyTheme {

    val colors: MyColors
        @ReadOnlyComposable
        @Composable
        get() = LocalColors.current

    val typography: MyTypography
        @ReadOnlyComposable
        @Composable
        get() = LocalTypography.current

    val isDark: Boolean
        @ReadOnlyComposable
        @Composable
        get() = LocalTheme.current
}

@Composable
fun MyTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (isDarkTheme) darkColors() else lightColors()
    CompositionLocalProvider(
        LocalColors provides colors,
        LocalTheme provides isDarkTheme,
        LocalTypography provides typography(),
        content = content
    )
}