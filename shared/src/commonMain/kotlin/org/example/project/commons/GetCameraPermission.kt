package org.example.project.commons

import androidx.compose.runtime.Composable

@Composable
expect fun GetCameraPermission(
    permissionGranted:(Boolean)-> Unit,
    showPermissionRationale: () -> Unit
)
interface CheckCameraPermGranted{
    fun checkCameraPermGranted(): Boolean
}