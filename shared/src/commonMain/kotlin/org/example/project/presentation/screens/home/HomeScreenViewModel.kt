package org.example.project.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.commons.CheckLocationStatus
import org.example.project.commons.logger

class HomeScreenViewModel(
    private val checkLocationStatus: CheckLocationStatus
): ViewModel() {

    private val _uiState = MutableStateFlow(HomeScreenUiState())
    val uiState = _uiState.asStateFlow()
    private val _uiEffects = MutableSharedFlow<HomeScreenUiEffects>(extraBufferCapacity = 1)
    val uiEffects = _uiEffects.asSharedFlow()

    fun onEvent(event: HomeScreenUiEvents){
        when(event){
            HomeScreenUiEvents.RealTimeTrackerClick -> {
                logger("Home Screen","RealTimeTrackerClick")
                if (checkLocationStatus.checkPermissionsAlreadyGranted()){
                    _uiState.update {
                        it.copy(
                            locationPermissionsGranted = true,
                            locationPermissionDialogShown = false
                        )
                    }
                    val permissionEnabledDisabled =  checkLocationStatus.checkIfLocationDisabled()
                    if (permissionEnabledDisabled){
                        viewModelScope.launch {
                            _uiEffects.emit(HomeScreenUiEffects.NavigateToRealTimeTracker)
                        }
                    }else{
                        _uiState.update {
                            it.copy(
                                showLocationEnabledDialog = true,
                            )
                        }
                    }
                }else{
                    _uiState.update {
                        it.copy(locationPermissionDialogShown = true)
                    }
                }
            }

            HomeScreenUiEvents.PermissionGranted -> {
                _uiState.update {
                    it.copy(
                        locationPermissionsGranted = true,
                        locationPermissionDialogShown = false
                    )
                }
                val permissionEnabledDisabled =  checkLocationStatus.checkIfLocationDisabled()
                if (permissionEnabledDisabled){
                    viewModelScope.launch {
                        _uiEffects.emit(HomeScreenUiEffects.NavigateToRealTimeTracker)
                    }
                }else{
                    _uiState.update {
                        it.copy(
                            showLocationEnabledDialog = true,
                        )
                    }
                }

            }

            HomeScreenUiEvents.AddYourFriendsClick -> {
                viewModelScope.launch {
                    _uiEffects.emit(HomeScreenUiEffects.NavigateToAddYourFriends)
                }
            }

            HomeScreenUiEvents.GoToEnableLocation -> {
                _uiState.update {
                    it.copy(
                        showLocationEnabledDialog = false,
                    )
                }
                checkLocationStatus.goToEnableLocationIfDisabled()
            }

            HomeScreenUiEvents.DismissLocationEnabledDialog -> {
                _uiState.update {
                    it.copy(
                        showLocationEnabledDialog = false,
                    )
                }
            }

            HomeScreenUiEvents.CheckLocationEnabledStatusOnStartUp -> {
                val permissionEnabledDisabled =  checkLocationStatus.checkIfLocationDisabled()
                _uiState.update {
                    it.copy(
                        locationEnabled = permissionEnabledDisabled,
                    )
                }
            }
        }
    }


}