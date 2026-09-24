package org.example.project.presentation.screens.createProfile

import org.example.project.domain.models.remote.User

data class CreateProfileUiState(
    val userName: String = "",
    val phoneNumber: String = "",
    val selectedImageUri: String? = null,
    val imageBase64: String? = null,
    val isImagePickerOpened: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val registeredUser: User? = null,
    val showAlreadyHaveAccountBtmSheet: Boolean = false,
    val alreadyHaveAccountUserName: String = "",
    val isLogInLoading: Boolean = false
) {
    val isSignUpEnabled: Boolean
        get() = userName.isNotBlank() && phoneNumber.isNotBlank() && !isLoading
}

sealed interface CreateProfileUiEvent {
    data class OnUserNameChanged(val userName: String) : CreateProfileUiEvent
    data class OnPhoneNumberChanged(val phoneNumber: String) : CreateProfileUiEvent
    data class OnImageSelected(val path: String, val base64: String) : CreateProfileUiEvent
    data object OnOpenImagePicker : CreateProfileUiEvent
    data object OnDismissImagePicker : CreateProfileUiEvent
    data object OnSignUpClicked : CreateProfileUiEvent
    data object OnAlreadyHaveAccountClicked : CreateProfileUiEvent
    data object OnClearError : CreateProfileUiEvent
    data class AlreadyHaveAccountUserNameChanged(val text: String) : CreateProfileUiEvent
    data class AlreadyHaveAccountUserLogIn(val userName: String) : CreateProfileUiEvent
    data object AlreadyHaveAccountBtmSheetDismiss : CreateProfileUiEvent

}

sealed interface CreateProfileUiEffect {
    data class NavigateToHome(val user: User) : CreateProfileUiEffect
    data object NavigateAsGuest : CreateProfileUiEffect
    data class ShowToast(val message: String) : CreateProfileUiEffect

}
