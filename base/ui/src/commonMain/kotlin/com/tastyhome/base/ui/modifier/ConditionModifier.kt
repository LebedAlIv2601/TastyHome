package com.tastyhome.base.ui.modifier

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Modifier.condition(
    condition: Boolean,
    ifTrue: @Composable Modifier.() -> Modifier,
    ifFalse: @Composable (Modifier.() -> Modifier) = { this }
): Modifier {
    return this then if (condition) ifTrue(Modifier) else ifFalse(Modifier)
}