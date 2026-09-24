package org.example.project.commons

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.example.project.presentation.screens.realtimetracker.SendRealTimeLocationForService
import platform.CoreLocation.CLAuthorizationStatus
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusNotDetermined
import platform.Foundation.NSBundle
import platform.Foundation.NSLog
import platform.darwin.NSObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(ExperimentalForeignApi::class)
class LocationServiceIOS : NativeLocationService, KoinComponent {

    private val realtimeTrackerViewModel by inject<SendRealTimeLocationForService>()

    private val locationManager = CLLocationManager()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var periodicJob: Job? = null
    private var lastKnownLocation: CLLocation? = null

    private val delegate = LocationDelegate(
        onLocationReceived = { location ->
            lastKnownLocation = location
            location.coordinate.useContents {
                val latitude = latitude
                val longitude = longitude

                logger("LocationService", "lat $latitude, lng $longitude")

                realtimeTrackerViewModel.sendMyLocation(
                    lat = latitude,
                    long = longitude
                )
            }
        },
        onAuthorizationChanged = { isAuthorized ->
            if (isAuthorized) {
                startTracking()
            }
        }
    )

    init {
        locationManager.delegate = delegate
        locationManager.desiredAccuracy = platform.CoreLocation.kCLLocationAccuracyBest
        locationManager.distanceFilter = platform.CoreLocation.kCLDistanceFilterNone
        locationManager.pausesLocationUpdatesAutomatically = false

        applyBackgroundConfiguration()
    }

    private fun applyBackgroundConfiguration() {
        val backgroundModes = NSBundle.mainBundle.objectForInfoDictionaryKey("UIBackgroundModes") as? List<*>
        if (backgroundModes?.contains("location") == true) {
            locationManager.allowsBackgroundLocationUpdates = true
            locationManager.showsBackgroundLocationIndicator = true
        } else {
            NSLog("Warning: UIBackgroundModes does not contain 'location'. Background location updates disabled.")
        }
    }

    override fun startService() {
        applyBackgroundConfiguration()

        val status = CLLocationManager.authorizationStatus()
        when (status) {
            kCLAuthorizationStatusAuthorizedAlways -> {
                startTracking()
            }

            kCLAuthorizationStatusAuthorizedWhenInUse -> {
                // Request Always upgrade so iOS allows location delivery in background after app minimizes
                locationManager.requestAlwaysAuthorization()
                startTracking()
            }

            kCLAuthorizationStatusNotDetermined -> {
                locationManager.requestAlwaysAuthorization()
            }

            else -> {
                locationManager.requestAlwaysAuthorization()
            }
        }
    }

    private fun startTracking() {
        applyBackgroundConfiguration()
        locationManager.startUpdatingLocation()
        try {
            locationManager.startMonitoringSignificantLocationChanges()
        } catch (e: Exception) {
            NSLog("startMonitoringSignificantLocationChanges error: ${e.message}")
        }
        startPeriodicLocationUpdates()
    }

    private fun startPeriodicLocationUpdates() {
        periodicJob?.cancel()
        periodicJob = scope.launch {
            while (isActive) {
                val loc = lastKnownLocation ?: locationManager.location
                if (loc != null) {
                    loc.coordinate.useContents {
                        val lat = latitude
                        val lng = longitude
                        logger("LocationServiceIOS", "5s periodic loc: lat $lat, lng $lng")
                        realtimeTrackerViewModel.sendMyLocation(
                            lat = lat,
                            long = lng
                        )
                    }
                } else {
                    locationManager.requestLocation()
                }
                delay(5000)
            }
        }
    }

    override fun stopService() {
        periodicJob?.cancel()
        periodicJob = null
        locationManager.stopUpdatingLocation()
        try {
            locationManager.stopMonitoringSignificantLocationChanges()
        } catch (e: Exception) {
            NSLog("stopMonitoringSignificantLocationChanges error: ${e.message}")
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private class LocationDelegate(
    private val onLocationReceived: (CLLocation) -> Unit,
    private val onAuthorizationChanged: (Boolean) -> Unit
) : NSObject(), CLLocationManagerDelegateProtocol {

    override fun locationManager(
        manager: CLLocationManager,
        didUpdateLocations: List<*>
    ) {
        val location = didUpdateLocations
            .lastOrNull() as? CLLocation
            ?: return

        onLocationReceived(location)
    }

    override fun locationManager(
        manager: CLLocationManager,
        didFailWithError: platform.Foundation.NSError
    ) {
        NSLog(
            "Location error: ${didFailWithError.localizedDescription}"
        )
    }

    override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
        val status = manager.authorizationStatus
        val isAuthorized = status == kCLAuthorizationStatusAuthorizedAlways ||
                status == kCLAuthorizationStatusAuthorizedWhenInUse
        onAuthorizationChanged(isAuthorized)
    }

    override fun locationManager(
        manager: CLLocationManager,
        didChangeAuthorizationStatus: CLAuthorizationStatus
    ) {
        val isAuthorized = didChangeAuthorizationStatus == kCLAuthorizationStatusAuthorizedAlways ||
                didChangeAuthorizationStatus == kCLAuthorizationStatusAuthorizedWhenInUse
        onAuthorizationChanged(isAuthorized)
    }
}