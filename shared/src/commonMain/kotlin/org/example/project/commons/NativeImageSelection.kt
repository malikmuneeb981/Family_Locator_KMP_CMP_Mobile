package org.example.project.commons

import androidx.compose.runtime.Composable

@Composable
expect fun SelectImage(
    selectedImage: (path: String,base64String: String) -> Unit
)