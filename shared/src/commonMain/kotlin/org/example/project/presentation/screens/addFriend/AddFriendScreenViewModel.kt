package org.example.project.presentation.screens.addFriend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.commons.AppUtils
import org.example.project.commons.CheckCameraPermGranted
import org.example.project.commons.logger
import org.example.project.domain.models.local.ConnectedFriends
import org.example.project.domain.models.remote.User
import org.example.project.domain.useCases.local.AddFriendToRoomUseCase
import org.example.project.domain.useCases.local.DeleteFriendFromRoomUseCase
import org.example.project.domain.useCases.local.GetAllFriendsFromRoomUseCase
import org.example.project.utils.DataStorePreferences
import org.example.project.utils.PreferencesKeys

class AddFriendScreenViewModel(
    private val dataStorePreferences: DataStorePreferences,
    private val addFriendToRoomUseCase: AddFriendToRoomUseCase,
    private val getAddFriendFromRoomUseCase: GetAllFriendsFromRoomUseCase,
    private val deleteFriendFromRoomUseCase: DeleteFriendFromRoomUseCase,
    private val checkCameraPermGranted: CheckCameraPermGranted,
    private val appUtils: AppUtils
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddFriendUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<AddFriendUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    fun getCurrentUserInfo() = viewModelScope.launch {
        val myName = dataStorePreferences
            .getObject<User>(PreferencesKeys.User_Object)
            .firstOrNull()
        myName?.let { nonNullUser->
            _uiState.update {
                it.copy(
                    myUserDetails = nonNullUser
                )
            }
        }
    }
    init {
        getCurrentUserInfo()
    }

    fun onEvent(event: AddFriendUiEvent) {
        when (event) {
            is AddFriendUiEvent.FriendsNameTextChanged -> {
                _uiState.update {
                    it.copy(
                        friendsUserName = event.text
                    )
                }
            }

            is AddFriendUiEvent.FriendsTextHideClick -> {
                _uiState.update {
                    it.copy(
                        isFriendNameTextVisible = event.isHidden
                    )
                }
            }

            is AddFriendUiEvent.AddNowBtnClick -> {
                logger("Add Friend ViewModel", "AddNowBtnClick")
                addConnectUsers(userName = event.userName)
            }

            is AddFriendUiEvent.ShowMyQRBtnClick -> {
                viewModelScope.launch {
                    _uiEffect.emit(AddFriendUiEffect.NavigateToShowMyQR)
                }
            }

            AddFriendUiEvent.ScanQRClick -> {
                if (checkCameraPermGranted.checkCameraPermGranted()){
                    viewModelScope.launch {
                        _uiEffect.emit(AddFriendUiEffect.NavigateToScanQROnCamPermGranted)
                    }
                }else{
                    _uiState.update {
                        it.copy(
                            getCameraPermission = true
                        )
                    }
                }

            }

            AddFriendUiEvent.BackBtnClick -> {
                viewModelScope.launch {
                    _uiEffect.emit(AddFriendUiEffect.NavigateBack)
                }
            }

            AddFriendUiEvent.GetAllConnectedFriendsFromRoom -> {
                getAllConnectedFriends()
            }

            is AddFriendUiEvent.FriendDeleteClicked -> {
                deleteConnectedFriend(connectedFriends = event.connectedFriends)
            }

            AddFriendUiEvent.ShowPermissionRationale -> {
                _uiState.update {
                    it.copy(
                        getCameraPermission = false
                    )
                }
            }

            AddFriendUiEvent.CameraPermissionGranted -> {
                _uiState.update {
                    it.copy(
                        getCameraPermission = false
                    )
                }
            }

            is AddFriendUiEvent.CopyTextToClipBoard -> {
                appUtils.nativeCopyTextToClipboard(text = event.text)
                viewModelScope.launch {
                    _uiEffect.emit(AddFriendUiEffect.ShowSnackBarTextCopied)
                }
            }
        }
    }
    private fun addConnectUsers(userName: String) = viewModelScope.launch(Dispatchers.IO) {
        _uiState.update {
            it.copy(
                userAdditionInProgress = true
            )
        }
        val id = addFriendToRoomUseCase.invoke(
            connectedFriends = ConnectedFriends(name = userName)
        )
        if (id == -1L) {
            _uiEffect.emit(
                AddFriendUiEffect.UserNotAddedShowError("Something Went Wrong...")
            )
        } else {
            _uiState.update {
                it.copy(
                    friendsUserName = ""
                )
            }
            _uiEffect.emit(
                AddFriendUiEffect.UserAddedSuccessfully
            )
        }
        _uiEffect.emit(AddFriendUiEffect.NavigateToRealtimeTracker)
    }
    private fun getAllConnectedFriends() = viewModelScope.launch {
        getAddFriendFromRoomUseCase.invoke().collect { friends ->
            logger("Realtime Tracker ViewModel", "Friends from room: $friends")
            _uiState.update {
                it.copy(connectedUsers = friends)
            }
        }
    }
    private fun deleteConnectedFriend(connectedFriends: ConnectedFriends) = viewModelScope.launch {
       val deletedRows =  deleteFriendFromRoomUseCase.invoke(connectedFriends = connectedFriends)
        if (deletedRows > 0) {
            _uiEffect.emit(
                AddFriendUiEffect.UserDeletionErrorShowError("Something Went Wrong...")
            )
        } else {
            _uiEffect.emit(
                AddFriendUiEffect.UserDeletedSuccessfully
            )
        }
    }




}