package org.example.project.presentation.screens.realtimetracker

import org.example.project.domain.models.remote.DisconnectRequest
import org.example.project.domain.models.remote.MyLatLng

    data class RealtimeTrackerUiState(
        val searchText: String="",
        val addFriendBtmSheetShowing: Boolean = false,
        val goToCurrentLocation: Boolean = true,
        // Latest position per friend, keyed by senderName internally, exposed as a list.
        val friendsLatLongs: List<MyLatLng> = emptyList(),
        val observedFriends : Set<String> = mutableSetOf(),
        val locateFriendLatLong : MyLatLng = MyLatLng(),
        val latestFriendPositions : Map<String, MyLatLng> = emptyMap()
    )
    sealed interface RealtimeTrackerUiEvents{
        data object BackClick:RealtimeTrackerUiEvents
        data object AddFriendsClick: RealtimeTrackerUiEvents
        data object MyLocationClick: RealtimeTrackerUiEvents

        data object NavigatedToLocation: RealtimeTrackerUiEvents
        data object PathIconClick: RealtimeTrackerUiEvents

        data class LocateMeClick(val myLatLng: MyLatLng): RealtimeTrackerUiEvents
        data class ShareMyLocation(val lat: Double,val long: Double): RealtimeTrackerUiEvents

        data class SearchTextChanged(val text: String): RealtimeTrackerUiEvents
        data object DisconnectUser: RealtimeTrackerUiEvents

        data object StartLocationService: RealtimeTrackerUiEvents
    }
    sealed interface RealtimeTrackerUiEffects{
        data object NavigateBack:RealtimeTrackerUiEffects

        data object NavigateToAddFriends:RealtimeTrackerUiEffects

    }
