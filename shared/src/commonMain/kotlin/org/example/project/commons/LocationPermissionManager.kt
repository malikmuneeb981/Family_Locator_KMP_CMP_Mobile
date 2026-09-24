package org.example.project.commons

import androidx.compose.runtime.Composable
import org.koin.core.module.Module

@Composable
expect fun LocationPermissionButton(
    onGranted: () -> Unit,
    onDenied: () -> Unit,
    showPermissionRationale: ()-> Unit
)
interface CheckLocationStatus{
    fun checkPermissionsAlreadyGranted(): Boolean
    fun checkIfLocationDisabled(): Boolean
    fun goToEnableLocationIfDisabled()
}