package org.example.project.presentation.screens.showMyQR

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.example.project.domain.models.remote.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.commons.GenerateQRFromUserName
import org.example.project.utils.DataStorePreferences
import org.example.project.utils.PreferencesKeys

class ShowMyQRViewModel(
    private val generateQRFromUserName: GenerateQRFromUserName,
    private val dataStorePreferences: DataStorePreferences
): ViewModel() {

    private val _uiState = MutableStateFlow(ShowMyQRUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<ShowMyQRUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    init {
        getUserNameAndGenerateQR()
    }
    fun getUserNameAndGenerateQR()=viewModelScope.launch(Dispatchers.IO) {
        _uiState.update {
            it.copy(isQRLoading = true)
        }
        dataStorePreferences.getObject<User>(PreferencesKeys.User_Object).collect {
            it?.let { user ->
                user.userName?.let { userName ->
                    val userQRCode =  generateQRFromUserName.generateQR(userName)
                    _uiState.update {
                        it.copy(isQRLoading = false,
                            myQR = userQRCode)
                    }
                }
            }

        }
    }

    fun onEvent(event: ShowMyQRUiEvent) {
        when (event) {
            ShowMyQRUiEvent.ScanQRClick -> {
                viewModelScope.launch {
                    _uiEffect.emit(ShowMyQRUiEffect.NavigateScanQR)
                }
            }

            ShowMyQRUiEvent.BackBtnClick -> {
                viewModelScope.launch {
                    _uiEffect.emit(ShowMyQRUiEffect.NavigateBack)
                }
            }
        }
    }

}