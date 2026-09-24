package org.example.project.commons

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
actual fun GetCameraPermission(
    permissionGranted: (Boolean) -> Unit,
    showPermissionRationale: () -> Unit
) {
    val launcher = rememberPermissionState(
        permission = Manifest.permission.CAMERA
    ){
        permissionGranted(it)
    }
    LaunchedEffect(Unit){
        when {
            launcher.status.isGranted->{
                permissionGranted(true)
            }
            launcher.status.shouldShowRationale->{
                showPermissionRationale()
            }
            else -> {
                launcher.launchPermissionRequest()
            }
        }
    }
}
class CheckCameraPermAndroid(private val context: Context):CheckCameraPermGranted{
    override fun checkCameraPermGranted(): Boolean {
        val camGranted =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED

        return camGranted
    }

}