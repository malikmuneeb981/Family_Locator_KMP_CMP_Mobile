package org.example.project.presentation.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier


@Composable
fun Modifier.nonRippleClick(onClick: () -> Unit) = Modifier.clickable(
    indication = null,
    interactionSource = remember { MutableInteractionSource() },
    onClick = {
        onClick()
    }
)