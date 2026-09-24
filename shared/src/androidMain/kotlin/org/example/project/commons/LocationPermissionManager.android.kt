package org.example.project.commons

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

@OptIn(ExperimentalPermissionsApi::class)
@Composable
actual fun LocationPermissionButton(onGranted: () -> Unit, onDenied: () -> Unit,showPermissionRationale: ()-> Unit) {
    val launcher = rememberMultiplePermissionsState(
        permissions = if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU)listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.POST_NOTIFICATIONS
        ) else listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
    ) { permissions ->

        val allGranted = permissions.values.all { it }

        if (allGranted) {
            onGranted()
        } else {
            onDenied()
        }

    }
    LaunchedEffect(Unit){
        when{
            launcher.allPermissionsGranted->{
                onGranted()
            }
            launcher.shouldShowRationale->{
                showPermissionRationale()
            }
            else->{
                launcher.launchMultiplePermissionRequest()
            }
        }
    }
}
class LocationPermissionStatusAndroid(val context: Context): CheckLocationStatus{
    override fun checkPermissionsAlreadyGranted(): Boolean {
        val fineGranted =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            val notificationGranted =
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            return fineGranted && coarseGranted && notificationGranted
        }else{
            return fineGranted && coarseGranted
        }
    }

    override fun checkIfLocationDisabled(): Boolean {
        val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val isLocationEnabled =
            LocationManagerCompat.isLocationEnabled(locationManager)
        return isLocationEnabled
    }

    override fun goToEnableLocationIfDisabled() {
        context.startActivity(
            Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
    }

}