package com.lebedaliv2601.base.ui.modifier

import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import kotlinx.coroutines.delay

@Composable
fun Modifier.clickableOnce(
    interactionSource: MutableInteractionSource,
    indication: Indication?,
    delayMillis: Long = DEFAULT_CLICKABLE_ONCE_DELAY,
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit
): Modifier {
    var enableAgain by remember { mutableStateOf(true) }
    LaunchedEffect(enableAgain, block = {
        if (enableAgain) return@LaunchedEffect
        delay(timeMillis = delayMillis)
        enableAgain = true
    })
    return this then Modifier.clickable(
        interactionSource = interactionSource,
        indication = indication,
        onClickLabel = onClickLabel,
        role = role,
        enabled = enabled,
    ) {
        if (enableAgain) {
            enableAgain = false
            onClick()
        }
    }
}

@Composable
fun Modifier.clickableOnce(
    enabled: Boolean = true,
    delayMillis: Long = DEFAULT_CLICKABLE_ONCE_DELAY,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit
): Modifier {
    return this then Modifier.clickableOnce(
        enabled = enabled,
        onClickLabel = onClickLabel,
        onClick = onClick,
        role = role,
        delayMillis = delayMillis,
        indication = LocalIndication.current,
        interactionSource = remember { MutableInteractionSource() }
    )
}

const val DEFAULT_CLICKABLE_ONCE_DELAY = 300L