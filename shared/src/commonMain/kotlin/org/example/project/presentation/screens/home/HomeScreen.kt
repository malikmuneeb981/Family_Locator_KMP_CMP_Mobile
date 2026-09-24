package org.example.project.presentation.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.translate.speech.to.text.dictionary.instant.voice.translator.presentation.composables.AppText
import familylocatorkmp.shared.generated.resources.Res
import familylocatorkmp.shared.generated.resources.add_your_frnds_bg_img
import familylocatorkmp.shared.generated.resources.crown_home
import familylocatorkmp.shared.generated.resources.ham_burger
import familylocatorkmp.shared.generated.resources.ic_enable_loc_dialog
import familylocatorkmp.shared.generated.resources.inter_bold
import familylocatorkmp.shared.generated.resources.inter_extrabold
import familylocatorkmp.shared.generated.resources.inter_medium
import familylocatorkmp.shared.generated.resources.inter_semibold
import familylocatorkmp.shared.generated.resources.live_img
import familylocatorkmp.shared.generated.resources.location_nearby_img
import familylocatorkmp.shared.generated.resources.realtime_tracker_img
import familylocatorkmp.shared.generated.resources.right_arrow
import familylocatorkmp.shared.generated.resources.saved_locations_img
import org.example.project.commons.CheckLocationStatus
import org.example.project.commons.LocationPermissionButton
import org.example.project.commons.logger
import org.example.project.domain.models.app.GenericDialogData
import org.example.project.presentation.composables.nonRippleClick
import org.example.project.presentation.dialogs.GenericDialog
import org.example.project.presentation.navigation.NavRoutes
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.getKoin

@Composable
fun HomeScreen(navController: NavController,
               homeScreenViewModel: HomeScreenViewModel){

    val uiState by homeScreenViewModel.uiState.collectAsStateWithLifecycle()
    logger("Home Screen","Home Screen Opened")
    if (uiState.showLocationEnabledDialog){
        GenericDialog(
            genericDialogData = GenericDialogData(
                image = Res.drawable.ic_enable_loc_dialog,
                text = "Location",
                secondaryText = "Location currently disabled Enable it from Settings",
                leftNegativeBtnText = "Cancel",
                rightPositiveBtnText = "Go to Settings"
            ), onDismiss = {
                homeScreenViewModel.onEvent(
                    HomeScreenUiEvents.DismissLocationEnabledDialog
                )
            }, onLeftNegativeBtnClick = {
                homeScreenViewModel.onEvent(
                    HomeScreenUiEvents.DismissLocationEnabledDialog
                )
            }, onRightPositiveBtnClick = {
                homeScreenViewModel.onEvent(
                    HomeScreenUiEvents.GoToEnableLocation
                )
            }
        )
    }
    if (uiState.locationPermissionDialogShown){
        LocationPermissionButton(onGranted = {
            homeScreenViewModel.onEvent(HomeScreenUiEvents.PermissionGranted)
        }, onDenied = {

        }, showPermissionRationale = {

        })
    }
    LaunchedEffect(Unit){
        homeScreenViewModel.onEvent(HomeScreenUiEvents.CheckLocationEnabledStatusOnStartUp)
    }
    LaunchedEffect(homeScreenViewModel){
        homeScreenViewModel.uiEffects.collect {
            when(it){
                HomeScreenUiEffects.NavigateToRealTimeTracker -> {
                    logger("Home Screen","Navigate to RealTime Tracker")
                    navController.navigate(NavRoutes.RealtimeTrackerScreen.route)
                }

                HomeScreenUiEffects.NavigateToAddYourFriends -> {
                    logger("Home Screen","Navigate to Add Friends Screen")
                    navController.navigate(NavRoutes.AddFriendScreen.route)
                }
            }
        }
    }
    Scaffold(topBar = {
        HomeTopBar()
    }, content = {

        Column(modifier = Modifier.fillMaxWidth().padding(it).padding(
            horizontal = 20.dp
        )) {
            RealTimeTracker(onClick = {
                logger("Home Screen","RealTimeTracker Click")
//                if (locationPermissionStatus.checkPermissionsAlreadyGranted()){
//                    logger("Home Screen","RealTimeTracker Permission Granted")
//                    homeScreenViewModel.onEvent(HomeScreenUiEvents.PermissionGranted)
//                }else{
                    logger("Home Screen","RealTimeTracker Permission Not Granted")
                    homeScreenViewModel.onEvent(HomeScreenUiEvents.RealTimeTrackerClick)
               // }
            })
            AppText(text = "Others",
                font = Res.font.inter_semibold,
                fontSize = 18.sp,
                modifier = Modifier.padding(
                    top = 22.dp
                ))
            Row(modifier = Modifier.fillMaxWidth().padding(
                top = 15.dp
            ),
                horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                OthersItem(image = Res.drawable.location_nearby_img,
                    text1 = "Location Nearby",
                    text2 = "See famous places around you")
                OthersItem(image = Res.drawable.saved_locations_img,
                    text1 = "Saved Locations",
                    text2 = "See your favourite locations")

            }
            AddYourFriends(onClick = {
                homeScreenViewModel.onEvent(
                    HomeScreenUiEvents.AddYourFriendsClick
                )
            })

        }
    }, containerColor = Color(0xFFF4F6FA))

}
//@Preview
//@Composable
//fun HomeScreenPrev(){
//    HomeScreen(rememberNavController())
//}
@Composable
fun RealTimeTracker(onClick:()-> Unit) {
    Row(modifier = Modifier.shadow(
        elevation = 15.dp,
        spotColor = Color(0xFF3268F0),
        shape = RoundedCornerShape(10.dp)
        ).clip(
        shape = RoundedCornerShape(10.dp)
    ).border(width = 1.dp,
        color = Color(0xFFD6E2FF),
        shape = RoundedCornerShape(10.dp)).background(color = Color.White).padding(
            start = 16.dp,
            top = 12.dp
        )
    ) {

        Column(modifier = Modifier.weight(1f).clickable(indication = null,
            interactionSource = remember { MutableInteractionSource() }, onClick = {
                onClick()
            })) {

            Image(vectorResource(Res.drawable.live_img),
                contentDescription = null)

            AppText(text = "Realtime Tracker",
                font = Res.font.inter_bold,
                fontSize = 17.sp, modifier = Modifier.padding(
                    top = 10.dp
                ), textAlign = TextAlign.Start
                )
            AppText(text = "Track your family and friends easily",
                fontSize = 10.sp,
                color = Color(0xFF7D7D8C), modifier = Modifier.padding(
                    top = 2.dp
                ), textAlign = TextAlign.Start
                )
            Box(modifier = Modifier.padding(
                top = 22.dp, bottom = 16.dp
            ).clip(
                shape = RoundedCornerShape(6.dp)
            ).background(
                color = Color(0xFF3268F0)
            ).clickable(indication = null,
                interactionSource = remember { MutableInteractionSource() },onClick = {

            })){
                AppText(text = "See Tracker",
                    color = Color.White,
                    fontSize = 12.sp,
                    font= Res.font.inter_semibold,
                    modifier = Modifier.padding(
                        vertical = 8.dp,
                        horizontal = 10.dp
                    ))
            }


        }
        Image(painterResource(Res.drawable.realtime_tracker_img),
            contentDescription = null,
            modifier = Modifier.weight(1f),
            contentScale = ContentScale.Crop)

    }
}
@Composable
fun RowScope.OthersItem(image: DrawableResource,text1: String,text2: String){
    Column(modifier = Modifier.weight(1f).background(
        color = Color(0xFF3268F0).copy(
            alpha = 0.08f
        ),
        shape = RoundedCornerShape(10.dp)
    ).padding(start = 16.dp,
        top = 16.dp,
        bottom = 16.dp,
        end = 5.dp)) {
        Image(vectorResource(image),
            contentDescription = null)
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier.padding(
                top = 15.dp
            )){
            AppText(text = text1,
                font = Res.font.inter_semibold,
                fontSize = 14.sp)
            Image(vectorResource(Res.drawable.right_arrow),
                contentDescription = null)
        }
        AppText(text = text2,
            font = Res.font.inter_medium,
            fontSize = 10.sp,
            color = Color(0xFF7D7D8C),
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(
                top = 4.dp
            ), textLines = 2,
            autoResize = true,
            minLines = 2)
    }


}
@Composable
fun AddYourFriends(onClick:()-> Unit){
    Box(modifier = Modifier.nonRippleClick {
        onClick()
    }.padding(
        top = 20.dp
    ).clip(
        shape = RoundedCornerShape(10.dp)
    )) {

        Image(vectorResource(Res.drawable.add_your_frnds_bg_img),
            contentDescription = null)

        Column(modifier = Modifier.fillMaxWidth(0.50f).padding(
            start = 16.dp,
            top = 16.dp,
            bottom = 16.dp
        )){
            AppText(text = "Add Your Friends",
                font = Res.font.inter_semibold,
                fontSize = 14.sp
                )
            AppText(text = "Use QR Code or invitation to see your friends here",
                font = Res.font.inter_medium,
                fontSize = 8.sp,
                color = Color(0xFF7D7D8C),
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(
                    top = 4.dp
                )
                )
            Box(modifier = Modifier.padding(
                top = 10.dp
            ).clip(
                shape = RoundedCornerShape(6.dp)
            ).background(
                color = Color(0xFF3268F0)
            ).clickable(indication = null,
                interactionSource = remember { MutableInteractionSource() },onClick = {

                })){
                AppText(text = "Add Now",
                    color = Color.White,
                    fontSize = 12.sp,
                    font= Res.font.inter_semibold,
                    modifier = Modifier.padding(
                        vertical = 8.dp,
                        horizontal = 10.dp
                    ))
            }
        }


    }
}
@Composable
fun HomeTopBar(){
    TopAppBar(
        navigationIcon = {
            Image(vectorResource(Res.drawable.ham_burger),
                contentDescription = null)
        },
        title = {
            AppText(text = "GPS/Location Tracker",
                font = Res.font.inter_semibold,
                fontSize = 17.sp)
        }, actions = {
            Row(modifier = Modifier.background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFFF3B0),
                        Color(0xFFCA26FF),
                        Color(0xFF3268F0)
                    )
                ),
                shape = RoundedCornerShape(30.dp)
            ).padding(vertical = 9.dp, horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)){
                Image(vectorResource(Res.drawable.crown_home),
                    contentDescription = null)
                AppText(text = "Get Pro",
                    font = Res.font.inter_extrabold,
                    color = Color.White,
                    fontSize = 11.sp)
            }
        }, colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        ), modifier = Modifier.padding(
            horizontal = 15.dp
        )
    )
}