package org.example.project.commons

import androidx.compose.ui.graphics.ImageBitmap

interface GenerateQRFromUserName {
    suspend fun generateQR(userName: String, size: Int = 512): ImageBitmap?
}