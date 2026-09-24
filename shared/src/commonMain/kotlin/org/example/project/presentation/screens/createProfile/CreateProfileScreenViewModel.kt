package org.example.project.presentation.screens.createProfile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.models.AuthRequest
import org.example.project.domain.models.remote.User
import io.ktor.client.call.body
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.commons.logger
import org.example.project.domain.useCases.remote.CreateProfileUseCases
import org.example.project.utils.DataStorePreferences
import org.example.project.utils.PreferencesKeys

class CreateProfileScreenViewModel(
    private val createProfileUseCases: CreateProfileUseCases,
    private val dataStorePreferences: DataStorePreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateProfileUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<CreateProfileUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    fun onEvent(event: CreateProfileUiEvent) {
        when (event) {
            is CreateProfileUiEvent.OnUserNameChanged -> {
                _uiState.update { it.copy(userName = event.userName, errorMessage = null) }
            }

            is CreateProfileUiEvent.OnPhoneNumberChanged -> {
                _uiState.update { it.copy(phoneNumber = event.phoneNumber, errorMessage = null) }
            }

            is CreateProfileUiEvent.OnImageSelected -> {
                _uiState.update {
                    it.copy(
                        selectedImageUri = event.path,
                        imageBase64 = event.base64,
                        isImagePickerOpened = false
                    )
                }
            }

            is CreateProfileUiEvent.OnOpenImagePicker -> {
                _uiState.update { it.copy(isImagePickerOpened = true) }
            }

            is CreateProfileUiEvent.OnDismissImagePicker -> {
                _uiState.update { it.copy(isImagePickerOpened = false) }
            }

            is CreateProfileUiEvent.OnSignUpClicked -> {
                signUp()
            }

            is CreateProfileUiEvent.OnAlreadyHaveAccountClicked -> {
                _uiState.update {
                    it.copy(
                        showAlreadyHaveAccountBtmSheet = true
                    )
                }
            }

            is CreateProfileUiEvent.OnClearError -> {
                _uiState.update { it.copy(errorMessage = null) }
            }

            is CreateProfileUiEvent.AlreadyHaveAccountUserNameChanged -> {
                _uiState.update {
                    it.copy(
                        alreadyHaveAccountUserName = event.text
                    )
                }
            }

            is CreateProfileUiEvent.AlreadyHaveAccountUserLogIn -> {
                checkUserExists(authRequest = AuthRequest(
                    userName = event.userName
                ))
            }

            CreateProfileUiEvent.AlreadyHaveAccountBtmSheetDismiss -> {
                _uiState.update {
                    it.copy(
                        showAlreadyHaveAccountBtmSheet = false
                    )
                }
            }
        }
    }

    private fun signUp() = viewModelScope.launch(Dispatchers.IO) {
        val currentState = _uiState.value
        if (currentState.userName.isBlank() || currentState.phoneNumber.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please fill in all fields") }
            return@launch
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        val user = User(
            userName = currentState.userName.trim(),
            phoneNumber = currentState.phoneNumber.trim(),
            userImage = currentState.imageBase64 ?: ""
        )

        try {
            val response = createProfileUseCases.signUpUserUseCase.invoke(user)
            logger("User request", "response: ${response.status}")

            if (response.status.value == 200) {
                val registeredUser = response.body<User>()

                // Persist session to DataStore
                dataStorePreferences.saveObject(PreferencesKeys.User_Object, registeredUser)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        registeredUser = registeredUser
                    )
                }
                registeredUser.userName?.let {
                    dataStorePreferences.saveString(PreferencesKeys.UserName,
                        registeredUser.userName
                    )
                    viewModelScope.launch {
                        _uiEffect.emit(CreateProfileUiEffect.NavigateToHome(registeredUser))
                    }
                }?:{
                    _uiState.update {
                        it.copy(
                            errorMessage = "Something Went Wrong"
                        )
                    }
                }

            } else {
                val errorMsg = "Sign up failed with status ${response.status.value}"
                _uiState.update { it.copy(isLoading = false, errorMessage = errorMsg) }
                _uiEffect.emit(CreateProfileUiEffect.ShowToast(errorMsg))
            }
        } catch (e: Exception) {
            val errorMsg = e.message ?: "An unexpected error occurred"
            logger("User request error", errorMsg)
            _uiState.update { it.copy(isLoading = false, errorMessage = errorMsg) }
            _uiEffect.emit(CreateProfileUiEffect.ShowToast(errorMsg))
        }
    }
    fun checkUserExists(authRequest: AuthRequest) = viewModelScope.launch(Dispatchers.IO) {
        try {
            _uiState.update {
                it.copy(isLogInLoading = true)
            }
            val response = createProfileUseCases.signAuthUseCase.invoke(authRequest)
            logger("Auth response", "response ${response.body<User>()}")
            if (response.status.value == 200) {
                val user = response.body<User>()
                dataStorePreferences.saveObject(PreferencesKeys.User_Object,user)
                _uiState.update {
                    it.copy(isLogInLoading = false)
                }
                _uiEffect.emit(CreateProfileUiEffect.NavigateToHome(user = user))
            } else {
                _uiState.update {
                    it.copy(isLogInLoading = false)
                }
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(isLogInLoading = false)
            }
        }
    }

}