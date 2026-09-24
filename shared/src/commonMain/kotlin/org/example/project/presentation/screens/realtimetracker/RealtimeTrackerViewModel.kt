package org.example.project.presentation.screens.realtimetracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.example.project.commons.NativeLocationService
import org.example.project.commons.logger
import org.example.project.domain.models.remote.DisconnectRequest
import org.example.project.domain.models.remote.MyLatLng
import org.example.project.domain.models.remote.User
import org.example.project.domain.useCases.local.GetAllFriendsFromRoomUseCase
import org.example.project.domain.useCases.remote.LocationUseCases
import org.example.project.utils.DataStorePreferences
import org.example.project.utils.PreferencesKeys

class RealtimeTrackerViewModel(
    private val locationUseCases: LocationUseCases,
    private val dataStorePreferences: DataStorePreferences,
    private val getAllFriendToRoomUseCase: GetAllFriendsFromRoomUseCase,
    private val nativeLocationService: NativeLocationService
): ViewModel() {
    private val _uiState = MutableStateFlow(RealtimeTrackerUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEffects = MutableSharedFlow<RealtimeTrackerUiEffects>()
    val uiEffects = _uiEffects.asSharedFlow()

    init {
        observeFriendsLatLongs()
        connectToAllUsers()
    }

    fun onEvent(event: RealtimeTrackerUiEvents){
        when(event){
            RealtimeTrackerUiEvents.AddFriendsClick -> {
                viewModelScope.launch {
                    _uiEffects.emit(RealtimeTrackerUiEffects.NavigateToAddFriends)
                }
            }
            RealtimeTrackerUiEvents.BackClick -> {
                viewModelScope.launch {
                    _uiEffects.emit(RealtimeTrackerUiEffects.NavigateBack)
                }
            }
            RealtimeTrackerUiEvents.MyLocationClick -> {
                _uiState.update {
                    it.copy(goToCurrentLocation = true)
                }
            }
            RealtimeTrackerUiEvents.PathIconClick -> {

            }

            is RealtimeTrackerUiEvents.SearchTextChanged -> {
                _uiState.update {
                    it.copy(
                        searchText = event.text
                    )
                }
            }

            RealtimeTrackerUiEvents.NavigatedToLocation -> {
                _uiState.update {
                    it.copy(goToCurrentLocation = false)
                }
            }

            is RealtimeTrackerUiEvents.ShareMyLocation -> {
                sendMyLocation(lat = event.lat, long = event.long)
            }

            is RealtimeTrackerUiEvents.LocateMeClick -> {
                _uiState.update {
                    it.copy(
                        locateFriendLatLong = event.myLatLng
                    )
                }
            }

            is RealtimeTrackerUiEvents.DisconnectUser -> {
                disconnectUser()
            }

            RealtimeTrackerUiEvents.StartLocationService -> {
                nativeLocationService.startService()
            }
        }
    }

    /**
     * Collects all incoming friend location updates from the single WebSocket stream.
     */
    private fun observeFriendsLatLongs() = viewModelScope.launch(Dispatchers.IO) {
        locationUseCases.observeAllFriendsLatLongsUseCase.invoke().collect { latLng ->
            logger("Incoming Latlongs", latLng.toString())
            val key = latLng.senderName ?: return@collect
            _uiState.update {
                val updatedMap = it.latestFriendPositions + (key to latLng)
                it.copy(
                    latestFriendPositions = updatedMap,
                    friendsLatLongs = updatedMap.values.toList()
                )
            }
        }
    }

    fun sendMyLocation(
        lat: Double,
        long: Double
    ) = viewModelScope.launch(Dispatchers.IO) {
        val myName = dataStorePreferences
            .getObject<User>(PreferencesKeys.User_Object)
            .firstOrNull()
            ?.userName
            ?: return@launch

        val friends = getAllFriendToRoomUseCase.invoke()
            .firstOrNull()
            ?: return@launch

        friends.forEach { friend ->
            logger("Realtime Viewmodel", "Sending loc to ${friend.name} $lat $long")
            locationUseCases.sendLocationUseCase(
                myLatLng = MyLatLng(
                    senderName = myName,
                    receiverName = friend.name,
                    latitude = lat,
                    longitude = long
                )
            )
        }
    }

    private fun connectToAllUsers() = viewModelScope.launch(Dispatchers.IO) {
        val myName = dataStorePreferences
            .getObject<User>(PreferencesKeys.User_Object)
            .firstOrNull()
            ?.userName ?: ""

        if (myName.isBlank()) return@launch

        // Start background keep-alive loop for the user's WebSocket connection
        launch {
            while (isActive) {
                locationUseCases.connectToUserUseCase.invoke(
                    myUserName = myName,
                    friendsUserName = "all"
                )
                delay(5000)
            }
        }

        // Keep friends list and observedFriends state in sync with local DB
        getAllFriendToRoomUseCase.invoke().collect { friends ->
            logger("Realtime Tracker ViewModel", "Friends from room: $friends")
            _uiState.update {
                it.copy(observedFriends = friends.map { f -> f.name }.toSet())
            }
        }
    }

    private fun disconnectUser() = viewModelScope.launch {
        val myName = dataStorePreferences
            .getObject<User>(PreferencesKeys.User_Object)
            .firstOrNull()
            ?.userName ?: ""
        logger("Realtime Tracker ViewModel", "Disconnected $myName")
        locationUseCases.disconnectUserFromWebSocketsUseCase(
            disconnectRequest = DisconnectRequest(userName = myName)
        )
    }
}