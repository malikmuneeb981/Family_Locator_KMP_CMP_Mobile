package org.example.project.presentation.screens.scanQR

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.domain.models.local.ConnectedFriends
import org.example.project.domain.useCases.local.AddFriendToRoomUseCase
import org.example.project.presentation.screens.addFriend.AddFriendUiEffect

class ScanQRScreenViewModel(
    private val addFriendToRoomUseCase: AddFriendToRoomUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScanQRUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<ScanQRUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()
    
    fun onEvent(event: ScanQRUiEvent) {
      when(event){
          is ScanQRUiEvent.ScannedQRText -> {
              if (event.text.isEmpty()){
                  ScanQRUiEffect.ShowErrorUserNotAdded("Something Went Wrong...")
              }else{
                  _uiState.update {
                      it.copy(scannedQRResult = event.text)
                  }
                  addConnectUsers(userName = event.text)
              }
          }
      }
    }
    private fun addConnectUsers(userName: String) = viewModelScope.launch(Dispatchers.IO) {
        val id = addFriendToRoomUseCase.invoke(
            connectedFriends = ConnectedFriends(name = userName)
        )
        if (id == -1L) {
            _uiEffect.emit(
                ScanQRUiEffect.ShowErrorUserNotAdded("Something Went Wrong...")
            )
            _uiEffect.emit(
                ScanQRUiEffect.QRScannedAndAddedToRoomNavigate
            )
        } else {
            _uiEffect.emit(
                ScanQRUiEffect.QRScannedAndAddedToRoomNavigate
            )
        }
    }





}