package org.example.project.commons

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun NativeQRScanner(
    modifier: Modifier = Modifier,
    onQRScannedResult:(String)-> Unit
)