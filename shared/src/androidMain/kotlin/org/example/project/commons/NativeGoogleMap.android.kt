package org.example.project.commons

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Looper
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberMarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.key
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.MapsInitializer
import com.google.maps.android.compose.MarkerInfoWindowContent
import familylocatorkmp.shared.generated.resources.Res
import familylocatorkmp.shared.generated.resources.ic_currentlocation
import familylocatorkmp.shared.generated.resources.user_click_marker
import org.example.project.domain.models.remote.MyLatLng
import org.example.project.presentation.composables.MarkerInfo
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
actual fun NativeGoogleMap(
    onMapClick: (lat: Double, long: Double) -> Unit,
    currentLocation:(lat: Double, long: Double)-> Unit,
    myFriendsLatLongs: List<MyLatLng>,
    goToCurrentLocation: Boolean,
    goToFriendsLatLong: MyLatLng,
    navigatedToCurrentLoc:()-> Unit
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val locationRequest = remember {
        LocationRequest.create().apply {
            interval = 5000
            fastestInterval = 2000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(33.6844, 73.0479), 16f)
    }
    val markerIcon = rememberBitmapDescriptor(Res.drawable.ic_currentlocation)
    val userClickMarkerIcon = rememberBitmapDescriptor(Res.drawable.user_click_marker)
    val friendMarkerIcon = rememberBitmapDescriptor(Res.drawable.user_click_marker)
    val markerState = rememberMarkerState()
    val userClickedMarkerState = rememberMarkerState()

    // Cache MarkerState instances by senderName so they are mutated in-place
    // rather than recreated whenever the list updates — avoids full marker recomposition.
    val friendMarkerStates = remember { mutableMapOf<String, MarkerState>() }

    LaunchedEffect(fusedLocationClient) {
        startLocationUpdates(fusedLocationClient, locationRequest) { location ->
            logger("Native Map","current loc $location")
            currentLocation = location
            currentLocation(location.latitude, location.longitude)
           // currentLocation(33.58667,73.124150)
        }
    }

    LaunchedEffect(currentLocation) {
        currentLocation?.let { loc ->
            markerState.position = loc
            markerState.showInfoWindow()
            if (goToCurrentLocation) {
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(loc, 16f)
                )
                navigatedToCurrentLoc()
            }
        }
    }

    LaunchedEffect(goToFriendsLatLong) {
        goToFriendsLatLong.let { loc ->
            loc.latitude?.let { lat->
                loc.longitude?.let { long->
                    val latLng = LatLng(lat, long)
                    val marker = friendMarkerStates.getOrPut(loc.senderName.toString()) {
                        MarkerState(position = latLng)
                    }
                    marker.position = latLng
                    marker.showInfoWindow()
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngZoom(latLng, 16f)
                    )
                }
            }
        }
    }

    // Update friend marker positions outside the composition lambda.
    // Mutating MarkerState.position is a snapshot write — it triggers only
    // the individual Marker that owns that state to recompose, not the whole map.
    LaunchedEffect(myFriendsLatLongs) {
        myFriendsLatLongs.forEach { latLng ->
            val key = latLng.senderName ?: return@forEach
            val lat = latLng.latitude ?: return@forEach
            val lng = latLng.longitude ?: return@forEach
            val state = friendMarkerStates.getOrPut(key) {
                MarkerState(position = LatLng(lat, lng))
            }
            state.position = LatLng(lat, lng)
        }
        // Remove stale keys for friends no longer in the list
        val activeKeys = myFriendsLatLongs.mapNotNull { it.senderName }.toSet()
        friendMarkerStates.keys.retainAll(activeKeys)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        uiSettings = MapUiSettings(
            myLocationButtonEnabled = false,
            zoomControlsEnabled = false,
            mapToolbarEnabled = false
        ),
        properties = MapProperties(
            isMyLocationEnabled = false
        ),
        onMapClick = {
            userClickedMarkerState.position = it
            onMapClick(it.latitude, it.longitude)
        },
        cameraPositionState = cameraPositionState
    ) {
        // My current location marker
        currentLocation?.let { loc ->
            MarkerInfoWindow(
                state = markerState,
                icon = markerIcon,
                onInfoWindowClick = {
                    logger("Native Map", "Marker Clicked")
                },
                content = {
                    MarkerInfo(lat = loc.latitude, long = loc.longitude) {
                        Log.d("Location Click", "Click 1")
                    }
                }
            )
        }

        // Map-click marker
        MarkerInfoWindow(
            state = userClickedMarkerState,
            icon = userClickMarkerIcon
        )

        // Friend markers — keyed by senderName so only the changed marker recomposes
        myFriendsLatLongs.forEach { latLng ->
            val key = latLng.senderName ?: return@forEach
            val state = friendMarkerStates[key] ?: return@forEach
            key(key) {
                Marker(
                    state = state,
                    icon = friendMarkerIcon,
                    title = latLng.senderName,
                    snippet = "Lat: ${state.position.latitude}, Lng: ${state.position.longitude}"
                )
            }
        }
    }
}

@SuppressLint("MissingPermission")
fun startLocationUpdates(
    fusedLocationClient: FusedLocationProviderClient,
    locationRequest: LocationRequest,
    currentLocation:(LatLng)-> Unit

    ) {
    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { location ->
                currentLocation(LatLng(location.latitude,location.longitude))
            }
        }
    }

    fusedLocationClient.requestLocationUpdates(
        locationRequest,
        locationCallback,
        Looper.getMainLooper()
    )
}


fun bitmapDescriptorFromPainter(painter: Painter, density: Density): BitmapDescriptor {
    val width = painter.intrinsicSize.width.let { if (it.isFinite() && it > 0) it.toInt() else 100 }
    val height = painter.intrinsicSize.height.let { if (it.isFinite() && it > 0) it.toInt() else 100 }
    val bitmap = Bitmap.createBitmap(
        width,
        height,
        Bitmap.Config.ARGB_8888
    )
    val canvas = androidx.compose.ui.graphics.Canvas(bitmap.asImageBitmap())
    CanvasDrawScope().draw(
        density = density,
        layoutDirection = LayoutDirection.Ltr,
        canvas = canvas,
        size = Size(width.toFloat(), height.toFloat())
    ) {
        with(painter) {
            draw(size = Size(width.toFloat(), height.toFloat()))
        }
    }
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

@Composable
fun rememberBitmapDescriptor(
    resource: DrawableResource,
    context: Context = LocalContext.current
): BitmapDescriptor? {
    val painter = painterResource(resource)
    val density = LocalDensity.current
    return remember(painter, density, context) {
        try {
            MapsInitializer.initialize(context)
            bitmapDescriptorFromPainter(painter, density)
        } catch (e: Exception) {
            null
        }
    }
}