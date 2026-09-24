package org.example.project.presentation.screens.onBoarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import org.example.project.domain.models.app.OnBoardingModel
import com.translate.speech.to.text.dictionary.instant.voice.translator.presentation.composables.AppText
import familylocatorkmp.shared.generated.resources.Res
import familylocatorkmp.shared.generated.resources.inter_medium
import familylocatorkmp.shared.generated.resources.inter_semibold
import familylocatorkmp.shared.generated.resources.onboarding_1
import familylocatorkmp.shared.generated.resources.onboarding_2
import familylocatorkmp.shared.generated.resources.onboarding_3
import org.example.project.presentation.composables.AppMainBtn
import org.example.project.presentation.navigation.NavRoutes
import org.jetbrains.compose.resources.painterResource

@Composable
fun OnBoardingScreen(navController: NavController){

    val pagesList = listOf(
        OnBoardingModel(
            index = 0,
            text1 = "Explore Smarter, Share Better",
            text2 = "Navigate effortlessly, stay connected with your friends, and share your location in just a few taps.",
            image = Res.drawable.onboarding_1,
        ),
        OnBoardingModel(
            index = 1,
            text1 = "Everything You Need, Right at Your Fingertips",
            text2 = "Real-Time Tracking, Nearby Locations, Friend Connections, and Save Addresses",
            image = Res.drawable.onboarding_2,
        ),
        OnBoardingModel(
            index = 2,
            text1 = "Set Up for a Seamless Experience",
            text2 = "Allow essential permissions for accurate tracking and personalized recommendations. Choose your language and connect your world",
            image = Res.drawable.onboarding_3
        ),
    )
    var currentIndex by remember { mutableIntStateOf(0) }

    val isLastItem by remember {
        derivedStateOf {
            currentIndex == pagesList.lastIndex
        }
    }

    val currentPage by remember {
        derivedStateOf {
            pagesList[currentIndex]
        }
    }
    Column(modifier = Modifier.fillMaxSize().background(
        color = Color(0xFFF4F6FA)
    ), horizontalAlignment = Alignment.CenterHorizontally) {
        OnBoardingItem(currentPage = currentPage)

        AppMainBtn(text = if (isLastItem) "Get Started" else "Continue",
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(
                bottom = 20.dp
            ), onClick = {
                if (isLastItem) {
                    navController.navigate(NavRoutes.CreateProfileScreen.route)
                } else {
                    currentIndex++
                }
            })

        AppText(text = "Skip",
            color = Color(0xFF7D7D8C),
            fontSize = 16.sp,
            font = Res.font.inter_medium,
            modifier = Modifier.padding(
                bottom = 40.dp
            ))

    }

}
@Composable
fun ColumnScope.OnBoardingItem(
    currentPage: OnBoardingModel
){
    Image(painterResource(currentPage.image),
        contentDescription = null,
        modifier = Modifier.fillMaxWidth().fillMaxHeight(0.5f),
        contentScale = ContentScale.Crop)

    Column(modifier = Modifier.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
        ) {
        AppText(text = currentPage.text1,
            modifier = Modifier.fillMaxWidth().padding(
                horizontal = 20.dp
            ),
            font = Res.font.inter_semibold,
            fontSize = 24.sp)
        AppText(text = currentPage.text2,
            modifier = Modifier.fillMaxWidth().padding(
                horizontal = 20.dp
            ).padding(top = 10.dp),
            color = Color(0xFF7D7D8C))
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 14.dp).padding(top = 20.dp)
        ) {
            repeat(3) { index ->
                val isSelected = currentPage.index == index
                Box(
                    modifier = Modifier
                        .width(if (isSelected) 32.dp else 10.dp)
                        .height(if (isSelected) 10.dp else 10.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) Color(0xFF003BEE)
                            else Color(0xFF003BEE).copy(
                                alpha =
                                    0.15f
                            )
                        )
                )
            }
        }

    }
}
@Preview
@Composable
fun OnBoardingScreenPrev(){
    OnBoardingScreen(rememberNavController())
}
//@Preview
//@Composable
//fun OnBoardingItemPrev(){
//    OnBoardingItem()
//}