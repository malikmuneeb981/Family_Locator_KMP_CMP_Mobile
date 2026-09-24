package org.example.project.presentation.screens.addFriend

import org.example.project.domain.models.local.ConnectedFriends
import org.example.project.domain.models.remote.User

data class AddFriendUiState(
    val friendsUserName: String = "",
    val isFriendNameTextVisible: Boolean = true,
    val addedUserNameList: List<User> = emptyList(),
    val myUserDetails: User = User(),
    val connectedUsers: List<ConnectedFriends> = emptyList(),
    val userAdditionInProgress: Boolean = false,
    val getCameraPermission: Boolean = false
) {
    val isAddFriendBtnEnabled: Boolean
        get() = friendsUserName.isNotBlank()
}

sealed interface AddFriendUiEvent {

    data class FriendsNameTextChanged(val text: String): AddFriendUiEvent
    data class FriendsTextHideClick(val isHidden: Boolean): AddFriendUiEvent
    data object ScanQRClick: AddFriendUiEvent

    data object BackBtnClick: AddFriendUiEvent
    data object ShowPermissionRationale: AddFriendUiEvent
    data object CameraPermissionGranted: AddFriendUiEvent
    data class AddNowBtnClick(val userName: String): AddFriendUiEvent
    data object ShowMyQRBtnClick:AddFriendUiEvent

    data class CopyTextToClipBoard(val text: String):AddFriendUiEvent

    data object GetAllConnectedFriendsFromRoom:AddFriendUiEvent

    data class FriendDeleteClicked(val connectedFriends: ConnectedFriends):AddFriendUiEvent
}

sealed interface AddFriendUiEffect {

    data object NavigateToShowMyQR : AddFriendUiEffect
    data object NavigateBack : AddFriendUiEffect
    data object NavigateToRealtimeTracker : AddFriendUiEffect

    data object UserAddedSuccessfully : AddFriendUiEffect
    data class UserNotAddedShowError(val message: String) : AddFriendUiEffect

    data object UserDeletedSuccessfully : AddFriendUiEffect
    data class UserDeletionErrorShowError(val message: String) : AddFriendUiEffect

    data object NavigateToScanQROnCamPermGranted : AddFriendUiEffect

    data object ShowSnackBarTextCopied:AddFriendUiEffect
}
