package org.example.project.presentation.screens.scanQR

data class ScanQRUiState(
   val scannedQRResult: String = ""
)

sealed interface ScanQRUiEvent {
   data class ScannedQRText(val text: String): ScanQRUiEvent
}

sealed interface ScanQRUiEffect {
   data object QRScannedAndAddedToRoomNavigate: ScanQRUiEffect
   data class ShowErrorUserNotAdded(val text: String): ScanQRUiEffect

}
