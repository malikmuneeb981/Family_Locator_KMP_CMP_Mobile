package org.example.project.commons

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.CoreLocation.CLAuthorizationStatus
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusDenied
import platform.CoreLocation.kCLAuthorizationStatusNotDetermined
import platform.CoreLocation.kCLAuthorizationStatusRestricted
import platform.darwin.NSObject

@Composable
actual fun LocationPermissionButton(
    onGranted: () -> Unit,
    onDenied: () -> Unit,
    showPermissionRationale: () -> Unit
) {
    val currentOnGranted = rememberUpdatedState(onGranted)
    val currentOnDenied = rememberUpdatedState(onDenied)
    val currentShowRationale = rememberUpdatedState(showPermissionRationale)

    val locationManager = remember { CLLocationManager() }

    val delegate = remember {
        object : NSObject(), CLLocationManagerDelegateProtocol {
            private fun handleStatus(status: CLAuthorizationStatus) {
                when (status) {
                    kCLAuthorizationStatusAuthorizedAlways,
                    kCLAuthorizationStatusAuthorizedWhenInUse -> {
                        currentOnGranted.value()
                    }
                    kCLAuthorizationStatusDenied,
                    kCLAuthorizationStatusRestricted -> {
                        currentOnDenied.value()
                    }
                    kCLAuthorizationStatusNotDetermined -> {
                        // User hasn't made a choice yet
                    }
                    else -> {}
                }
            }

            override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
                handleStatus(manager.authorizationStatus)
            }

            override fun locationManager(
                manager: CLLocationManager,
                didChangeAuthorizationStatus: CLAuthorizationStatus
            ) {
                handleStatus(didChangeAuthorizationStatus)
            }
        }
    }

    DisposableEffect(locationManager, delegate) {
        locationManager.delegate = delegate
        onDispose {
            locationManager.delegate = null
        }
    }

    LaunchedEffect(Unit) {
        when (locationManager.authorizationStatus) {
            kCLAuthorizationStatusAuthorizedAlways,
            kCLAuthorizationStatusAuthorizedWhenInUse -> {
                currentOnGranted.value()
            }
            kCLAuthorizationStatusNotDetermined -> {
                locationManager.requestWhenInUseAuthorization()
            }
            kCLAuthorizationStatusDenied,
            kCLAuthorizationStatusRestricted -> {
                currentShowRationale.value()
            }
            else -> {
                locationManager.requestWhenInUseAuthorization()
            }
        }
    }
}
class LocationPermissionStatusIOS : CheckLocationStatus {

    override fun checkPermissionsAlreadyGranted(): Boolean {
        val status = CLLocationManager.authorizationStatus()
        return when (status) {
            kCLAuthorizationStatusAuthorizedAlways,
            kCLAuthorizationStatusAuthorizedWhenInUse -> {
                true
            }

            else -> {
                false
            }
        }
    }

    override fun checkIfLocationDisabled(): Boolean {
        return CLLocationManager.locationServicesEnabled()
    }

    override fun goToEnableLocationIfDisabled() {
        val url = platform.Foundation.NSURL.URLWithString(platform.UIKit.UIApplicationOpenSettingsURLString)
        if (url != null) {
            platform.UIKit.UIApplication.sharedApplication.openURL(url)
        }
    }
}