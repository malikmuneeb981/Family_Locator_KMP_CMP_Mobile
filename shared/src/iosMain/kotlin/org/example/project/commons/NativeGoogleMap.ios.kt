package org.example.project.commons

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import org.example.project.domain.models.remote.MyLatLng
import platform.UIKit.UIView

/**
 * Factory assigned from Swift in iOSApp.swift to inject Google Maps (GMSMapView).
 *
 * Parameters flowing Kotlin → Swift use Kotlin [Double], which bridges to [KotlinDouble]
 * on the Swift side (e.g. `KotlinDouble(value: coordinate.latitude)`).
 */
var googleMapViewFactory: ((
    onMapClick: (lat: Double, long: Double) -> Unit,
    currentLocation: (lat: Double, long: Double) -> Unit,
    goToCurrentLocation: Boolean,
    navigatedToCurrentLoc: () -> Unit
) -> UIView)? = null

/**
 * Update function assigned from Swift in iOSApp.swift.
 *
 * Called from the [UIKitView] `update` block on every recomposition — mirrors
 * Android's `LaunchedEffect(myFriendsLatLongs)`, `LaunchedEffect(currentLocation)`, and
 * `LaunchedEffect(goToFriendsLatLong)` patterns by pushing new state into the
 * already-created [GMSMapView] holder.
 *
 * [goToFriendsLatLong] corresponds to Android's camera-jump to a selected friend's location.
 */
var googleMapViewUpdate: ((
    view: UIView,
    friends: List<MyLatLng>,
    goToCurrentLocation: Boolean,
    goToFriendsLatLong: MyLatLng,
    navigatedToCurrentLoc: () -> Unit
) -> Unit)? = null

@Composable
actual fun NativeGoogleMap(
    onMapClick: (lat: Double, long: Double) -> Unit,
    currentLocation: (lat: Double, long: Double) -> Unit,
    myFriendsLatLongs: List<MyLatLng>,
    goToCurrentLocation: Boolean,
    goToFriendsLatLong: MyLatLng,
    navigatedToCurrentLoc: () -> Unit
) {
    val currentOnMapClick = rememberUpdatedState(onMapClick)
    val currentLoc        = rememberUpdatedState(currentLocation)
    val currentNavigated  = rememberUpdatedState(navigatedToCurrentLoc)
    val customFactory     = googleMapViewFactory
    val updateMap         = googleMapViewUpdate

    if (customFactory != null) {
        UIKitView(
            factory = {
                customFactory(
                    { lat, long -> currentOnMapClick.value(lat, long) },
                    { lat, long -> currentLoc.value(lat, long) },
                    goToCurrentLocation,
                    { currentNavigated.value() }
                )
            },
            // update is called on every recomposition — mirrors Android's LaunchedEffect pattern
            update = { view ->
                updateMap?.invoke(
                    view,
                    myFriendsLatLongs,
                    goToCurrentLocation,
                    goToFriendsLatLong,
                    { currentNavigated.value() }
                )
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}