package org.example.project.domain.models.app

import org.jetbrains.compose.resources.DrawableResource

data class OnBoardingModel(
    val index: Int,
    val text1: String,
    val text2: String,
    val image: DrawableResource,
)