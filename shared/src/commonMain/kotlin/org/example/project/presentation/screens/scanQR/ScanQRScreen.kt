package org.example.project.presentation.screens.scanQR

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import org.example.project.commons.NativeQRScanner
import org.example.project.commons.logger
import org.example.project.presentation.composables.GenericTopBar
import org.example.project.presentation.navigation.NavRoutes

@Composable
fun ScanQRScreen(navController: NavController,scanQRScreenViewModel: ScanQRScreenViewModel){
    val uiState by scanQRScreenViewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(scanQRScreenViewModel){
        scanQRScreenViewModel.uiEffect.collect {
            when(it){
                ScanQRUiEffect.QRScannedAndAddedToRoomNavigate -> {
                    navController.navigate(NavRoutes.AddFriendScreen.route)
                }

                is ScanQRUiEffect.ShowErrorUserNotAdded -> {

                }
            }
        }

    }
    Scaffold(topBar = {
        GenericTopBar(text = "Scan QR",
            onBackClick = {
                navController.navigate(NavRoutes.AddFriendScreen.route)
            },showScan = false)
    }, content = {
        Column(modifier = Modifier.fillMaxSize().padding(it)){
            NativeQRScanner ( modifier = Modifier.fillMaxSize(), onQRScannedResult = { scannedText->
                logger("Scan QR Screen","Scanned QR : $scannedText")
                scanQRScreenViewModel.onEvent(
                    ScanQRUiEvent.ScannedQRText(scannedText)
                )
            })
        }

    }, containerColor = Color(0xFFF4F6FA))
}

//@Preview
//@Composable
//fun ScanQRScreenPrev(){
//    ScanQRScreen(rememberNavController(),
//        scanQRScreenViewModel = ScanQRScreenViewModel())
//}