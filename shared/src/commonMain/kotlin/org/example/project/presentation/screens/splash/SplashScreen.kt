package org.example.project.presentation.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.translate.speech.to.text.dictionary.instant.voice.translator.presentation.composables.AppText
import familylocatorkmp.shared.generated.resources.Res
import familylocatorkmp.shared.generated.resources.inter_extrabold
import familylocatorkmp.shared.generated.resources.splash_img_center
import familylocatorkmp.shared.generated.resources.splash_img_left
import familylocatorkmp.shared.generated.resources.splash_img_right
import familylocatorkmp.shared.generated.resources.splash_scenery
import kotlinx.coroutines.delay
import org.example.project.commons.logger
import org.example.project.presentation.composables.AppMainBtn
import org.example.project.presentation.navigation.NavRoutes
import org.jetbrains.compose.resources.painterResource
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SplashScreen(navController: NavController,
                 splashViewModel: SplashViewModel){

    val uiEvents by splashViewModel.uiState.collectAsStateWithLifecycle()
    Column(modifier = Modifier.fillMaxSize().background(
        color = Color(0xFFF4F6FA)
    )){

        LaunchedEffect(splashViewModel){
            splashViewModel.onEvent(SplashUiEvents.ShowLoading)
            splashViewModel.uiEffects.collect {
                when(it){
                    SplashUiEffects.NavigateToHome -> {
                        navController.navigate(NavRoutes.HomeScreen.route)
                        logger("Splash Screen","Navigate to Home")
                    }
                    SplashUiEffects.NavigateToOnBoarding -> {
                        navController.navigate(NavRoutes.OnBoardingScreen.route)
                    }
                    SplashUiEffects.ShowBtn -> {

                    }
                    is SplashUiEvents.CheckForUserAuth -> {

                    }
                }
            }
        }
        Box(modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.BottomCenter){
            Image(
                painterResource(Res.drawable.splash_scenery),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Crop)
            Row(modifier = Modifier.fillMaxWidth().padding(
                bottom = 5.dp
            ), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround) {

                Image(painterResource(Res.drawable.splash_img_left),
                    contentDescription = null)

                Image(painterResource(Res.drawable.splash_img_center),
                    contentDescription = null)

                Image(painterResource(Res.drawable.splash_img_right),
                    contentDescription = null)
            }

        }

        AppText(text = "GPS/Location Tracker",
            font = Res.font.inter_extrabold,
            fontSize = 30.sp,
            modifier = Modifier.fillMaxWidth().padding(
                top = 20.dp
            ))
        AppText(text = "Track your friends, family and loved one",
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF7D7D8C),
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp))

        Box(modifier = Modifier.weight(1f).fillMaxWidth(),
            contentAlignment = Alignment.Center){
            if (uiEvents.isLoading){
                CircularProgressIndicator(
                    color = Color(0xFF3268F0),

                )
            }else{
                AppMainBtn(text = "Get Started",
                    modifier = Modifier.fillMaxWidth().padding(
                        horizontal = 20.dp
                    ), onClick = {
                        splashViewModel.onEvent(SplashUiEvents.GetStartedBtnClick)
                    })
            }
        }

    }


}
@Preview
@Composable
fun SplashScreenPrev(){
   // SplashScreen(rememberNavController())
}