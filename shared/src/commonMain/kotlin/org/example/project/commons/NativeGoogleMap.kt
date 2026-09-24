package org.example.project.commons

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import org.example.project.domain.models.remote.MyLatLng

@Composable
expect fun NativeGoogleMap(
    onMapClick:(lat: Double, long: Double)-> Unit,
    currentLocation:(lat: Double, long: Double)-> Unit,
    myFriendsLatLongs: List<MyLatLng>,
    goToCurrentLocation: Boolean,
    goToFriendsLatLong: MyLatLng,
    navigatedToCurrentLoc:()-> Unit
)