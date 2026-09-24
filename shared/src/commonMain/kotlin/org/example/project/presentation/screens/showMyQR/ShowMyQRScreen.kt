package org.example.project.presentation.screens.showMyQR

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.translate.speech.to.text.dictionary.instant.voice.translator.presentation.composables.AppText
import familylocatorkmp.shared.generated.resources.Res
import familylocatorkmp.shared.generated.resources.inter_medium
import familylocatorkmp.shared.generated.resources.qr_code
import org.example.project.presentation.composables.AppMainBtn
import org.example.project.presentation.composables.GenericTopBar
import org.example.project.presentation.navigation.NavRoutes
import org.jetbrains.compose.resources.painterResource

@Composable
fun ShowMyQRScreen(
    navController: NavController,
    showMyQRViewModel: ShowMyQRViewModel
){
    val uiState by showMyQRViewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(showMyQRViewModel){
        showMyQRViewModel.uiEffect.collect {
            when(it){
                ShowMyQRUiEffect.NavigateBack -> {
                    navController.navigate(NavRoutes.AddFriendScreen.route)
                }

                ShowMyQRUiEffect.NavigateScanQR -> {
                    navController.navigate(NavRoutes.ScanQRScreen.route)
                }
            }
        }
    }
    Scaffold (
        topBar = {
            GenericTopBar(text = "QR Code",
                onBackClick = {
                    showMyQRViewModel.onEvent(
                        ShowMyQRUiEvent.BackBtnClick
                    )
                },showScan = false)
        },
        content = {
            Column(modifier = Modifier.fillMaxSize().padding(it).padding(
                horizontal = 20.dp
            ), horizontalAlignment = Alignment.CenterHorizontally){
                if (uiState.isQRLoading){
                    CircularProgressIndicator(
                        modifier = Modifier.padding(bottom = 20.dp),
                        color = Color(0xFF3268F0)
                    )
                }else{
                    uiState.myQR?.let { qrImage->
                        Image(
                            bitmap = qrImage,
                            contentDescription = null,
                            modifier = Modifier.padding(
                                top = 40.dp
                            ).size(300.dp)
                        )
                    }
                }
                AppText(text = "Your QR code is private. If you share it with someone,they can scan it with their GPS Tracker camera to addyou as a friend.",
                    color = Color(0xFF7D7D8C),
                    font = Res.font.inter_medium,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(
                        top = 30.dp
                    ))
                Column(modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.Center){
                    AppMainBtn(
                        text = "Scan",
                        onClick = {
                            showMyQRViewModel.onEvent(
                                ShowMyQRUiEvent.ScanQRClick
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }, containerColor = Color(0xFFF4F6FA),
    )
}
@Preview
@Composable
fun ShowMyQRScreenPrev(){
 //   ShowMyQRScreen(rememberNavController())
}