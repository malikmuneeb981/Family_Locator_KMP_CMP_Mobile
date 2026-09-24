package org.example.project.presentation.screens.showMyQR

import androidx.compose.ui.graphics.ImageBitmap

data class ShowMyQRUiState(
    val myQR:ImageBitmap?=null,
    val isQRLoading: Boolean = true
) {
}

sealed interface ShowMyQRUiEvent {
    data object ScanQRClick: ShowMyQRUiEvent
    data object BackBtnClick: ShowMyQRUiEvent
 
}

sealed interface ShowMyQRUiEffect {
    data object NavigateBack: ShowMyQRUiEffect
    data object NavigateScanQR: ShowMyQRUiEffect
}
