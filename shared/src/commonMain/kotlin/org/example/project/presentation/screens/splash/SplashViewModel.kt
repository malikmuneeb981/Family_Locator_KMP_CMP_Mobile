package org.example.project.presentation.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.models.AuthRequest
import org.example.project.domain.models.remote.User
import io.ktor.client.call.body
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.commons.logger
import org.example.project.domain.useCases.remote.SignAuthUseCase
import org.example.project.utils.DataStorePreferences
import org.example.project.utils.PreferencesKeys

class SplashViewModel(
    private val signAuthUseCase: SignAuthUseCase,
    private val dataStorePreferences: DataStorePreferences
): ViewModel() {

    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEffects = MutableSharedFlow<SplashUiEffects>()
    val uiEffects = _uiEffects.asSharedFlow()

    fun onEvent(splashUiEvents: SplashUiEvents){
        when(splashUiEvents){
            SplashUiEvents.ShowLoading -> {
                _uiState.update {
                    it.copy(isLoading = true)
                }
                getUserName()
            }
            SplashUiEvents.HideLoading -> {
                _uiState.update {
                    it.copy(isLoading = false)
                }
            }

            SplashUiEvents.GetStartedBtnClick -> {
                viewModelScope.launch {
                    if (uiState.value.userAuthenticated){
                        _uiEffects.emit(SplashUiEffects.NavigateToHome)
                    }else{
                        _uiEffects.emit(SplashUiEffects.NavigateToOnBoarding)
                    }
                }
            }
        }
    }
    fun getUserName() = viewModelScope.launch {
        dataStorePreferences.getObject<User>(PreferencesKeys.User_Object).collect {
            logger("Auth UserName", "$it")
            if (!it?.userName.isNullOrEmpty()){
                checkUserExists(AuthRequest(
                    userName = it.userName
                ))
            } else{
                _uiState.update {
                    it.copy(isLoading = false)
                }
            }
        }
    }
    fun checkUserExists(authRequest: AuthRequest) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val response = signAuthUseCase.invoke(authRequest)
            logger("Auth response", "response ${response.body<User>()}")
            if (response.status.value == 200) {
                val user = response.body<User>()
                dataStorePreferences.saveObject(PreferencesKeys.User_Object,user)
                _uiState.update {
                    it.copy(isLoading = false,
                        userAuthenticated = true)
                }
            } else {
                _uiState.update {
                    it.copy(isLoading = false,
                        userAuthenticated = false)
                }
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(isLoading = false,
                    userAuthenticated = false)
            }
        }
    }

}