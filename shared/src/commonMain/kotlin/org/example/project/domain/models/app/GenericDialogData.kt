package org.example.project.domain.models.app

import org.jetbrains.compose.resources.DrawableResource

data class GenericDialogData(
    val image: DrawableResource,
    val text: String,
    val secondaryText: String,
    val leftNegativeBtnText: String,
    val rightPositiveBtnText: String,
)
