package org.example.project.presentation.screens.realtimetracker

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.translate.speech.to.text.dictionary.instant.voice.translator.presentation.composables.AppText
import familylocatorkmp.shared.generated.resources.Res
import familylocatorkmp.shared.generated.resources.ic_add_friend
import familylocatorkmp.shared.generated.resources.ic_avatar
import familylocatorkmp.shared.generated.resources.ic_back_realtime_tracker
import familylocatorkmp.shared.generated.resources.ic_my_location
import familylocatorkmp.shared.generated.resources.ic_path
import familylocatorkmp.shared.generated.resources.inter_medium
import familylocatorkmp.shared.generated.resources.inter_semibold
import kotlinx.coroutines.delay
import org.example.project.commons.NativeGoogleMap
import org.example.project.commons.logger
import org.example.project.presentation.composables.nonRippleClick
import org.example.project.domain.models.remote.MyLatLng
import org.example.project.presentation.navigation.NavRoutes
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun RealtimeTrackerScreen(
    navController: NavController,
    realtimeTrackerViewModel: RealtimeTrackerViewModel
){
    val uiState by realtimeTrackerViewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(realtimeTrackerViewModel){
        realtimeTrackerViewModel.onEvent(
            RealtimeTrackerUiEvents.StartLocationService
        )
        realtimeTrackerViewModel.uiEffects.collect {
            when(it){
                RealtimeTrackerUiEffects.NavigateBack -> {
                    logger("RealTime Tracker","Navigate Back")
                    navController.navigate(NavRoutes.HomeScreen.route)
                }

                RealtimeTrackerUiEffects.NavigateToAddFriends -> {
                    navController.navigate(NavRoutes.AddFriendScreen.route)
                }
            }
        }
    }

    DisposableEffect(realtimeTrackerViewModel){
        onDispose {
            realtimeTrackerViewModel.onEvent(
                RealtimeTrackerUiEvents.DisconnectUser
            )
        }
    }
    Box(modifier = Modifier.fillMaxSize()){

        NativeGoogleMap(onMapClick = { lat,long->

        }, currentLocation = { lat,long->
            realtimeTrackerViewModel.onEvent(
                RealtimeTrackerUiEvents.ShareMyLocation(
                    lat = lat,
                    long = long
                )
            )
        }, goToCurrentLocation = uiState.goToCurrentLocation,
            goToFriendsLatLong = uiState.locateFriendLatLong,
            navigatedToCurrentLoc = {
                realtimeTrackerViewModel.onEvent(RealtimeTrackerUiEvents.NavigatedToLocation)
            }, myFriendsLatLongs = uiState.friendsLatLongs)
//        Box(modifier = Modifier.fillMaxSize().background(
//            color = Color.White
//        )){
//
//        }
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.End){
            TopBar(text = uiState.searchText, onValueChange = {
                realtimeTrackerViewModel.onEvent(
                    RealtimeTrackerUiEvents.SearchTextChanged(it)
                )
            }, onBackClick = {
                logger("RealTime Tracker","Navigate Back")
                realtimeTrackerViewModel.onEvent(
                    RealtimeTrackerUiEvents.BackClick
                )
            })
            Spacer(modifier = Modifier.weight(1f).fillMaxWidth())
            RoundButtons(modifier = Modifier.padding(
                end = 20.dp
            ).clip(
                CircleShape
            ).background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF3268F0),
                        Color(0xFF012FA2),
                    )
                ),
                shape = CircleShape
            ).clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = {
                    realtimeTrackerViewModel.onEvent(RealtimeTrackerUiEvents.MyLocationClick)
                }
            ).padding(
                10.dp
            ), image = Res.drawable.ic_my_location)
            RoundButtons(modifier = Modifier.padding(
                end = 20.dp,
                top = 16.dp
            ).shadow(
                elevation = 5.dp,
                shape = CircleShape
            ).background(
                color = Color.White,
                shape = CircleShape
            ).padding(
                10.dp
            ), image = Res.drawable.ic_path)

            AddFriendsBtmSheet(friendsNamesAndLatLongs = uiState.friendsLatLongs, onLocateMeClick = {
                realtimeTrackerViewModel.onEvent(
                    RealtimeTrackerUiEvents.LocateMeClick(it)
                )
            }, onAddFriendClick = {

            })
        }


    }


}
@Composable
fun TopBar(text: String,onValueChange:(String)-> Unit,
           onBackClick:()-> Unit){
    Row(modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(
        horizontal = 20.dp
    ).padding(
        top = 17.dp
    ), horizontalArrangement = Arrangement.spacedBy(
        20.dp
    ))
    {
        Box(modifier = Modifier.nonRippleClick(onClick = {
            logger("RealTime Tracker","Navigate Back Click")
            onBackClick()
        }).shadow(elevation = 5.dp,
            shape = CircleShape).background(color = Color.White,
            shape = CircleShape
        ).padding(
            10.dp
        )){
            Image(vectorResource(Res.drawable.ic_back_realtime_tracker),
                contentDescription = null)
        }
        Box(modifier = Modifier.weight(1f).shadow(elevation = 5.dp,
            shape = CircleShape).background(color = Color.White,
            shape = CircleShape
        )){
            BasicTextField(
                value = text,
                onValueChange = {
                    onValueChange(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(43.dp)
                    .background(Color.White),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 16.sp
                ),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {

                        if (text.isEmpty()) {
                            AppText(
                                text = "Search",
                                color = Color(0xFF7D7D8C),
                                font = Res.font.inter_medium,
                                fontSize = 14.sp
                            )
                        }

                        innerTextField()
                    }
                }
            )
        }
    }
}
@Composable
fun RoundButtons(modifier: Modifier= Modifier, image: DrawableResource){
    Box(modifier = modifier){
        Image(vectorResource(image),
            contentDescription = null)
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFriendsBtmSheet(
    friendsNamesAndLatLongs: List<MyLatLng>,
    onLocateMeClick:(MyLatLng) -> Unit,
    onAddFriendClick:()-> Unit
){
    val showBtmSheet by remember { mutableStateOf(true) }
//    val sheetState = rememberModalBottomSheetState(
//        skipPartiallyExpanded = true,
//        confirmValueChange = { newValue ->
//            newValue != SheetValue.Hidden
//        }
//    )
    if (showBtmSheet)
    {
//        ModalBottomSheet(sheetState = sheetState,onDismissRequest = {
//
//        }, properties = ModalBottomSheetProperties(
//            shouldDismissOnBackPress = false,
//            shouldDismissOnClickOutside = false
//        ), dragHandle = {
//
//        },
//        ){

            Column(modifier = Modifier.padding(
                top = 15.dp
            ).shadow(
                elevation = 25.dp,
                shape =  RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp
                )
            ).padding(
                top = 5.dp
            ).background(
                color = Color.White,
                shape = RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp
                )
            ).fillMaxWidth().height(280.dp)) {

                Row(modifier = Modifier.fillMaxWidth().padding(
                    top = 24.dp
                ).padding(horizontal = 20.dp)) {

                    AppText(text = "Friends List",
                        font = Res.font.inter_semibold,
                        fontSize = 19.sp,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Start)

                    Image(vectorResource(Res.drawable.ic_add_friend),
                        contentDescription = null, modifier = Modifier.nonRippleClick(onClick = {
                            onAddFriendClick()
                        }))

                }
                Box(modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center){
                    if (friendsNamesAndLatLongs.isEmpty()){
                            AppText(text = "You haven’t added any friend yet.\nPlease Click on “User Plus” Icon to add here.",
                                font = Res.font.inter_medium,
                                fontSize = 14.sp,
                                color = Color(0xFF7D7D8C),
                                modifier = Modifier.fillMaxWidth())


                    }else{
                        LazyColumn(modifier = Modifier.fillMaxWidth().matchParentSize(),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            contentPadding = PaddingValues(vertical = 15.dp,
                                horizontal = 20.dp)
                        ) {
                            items(friendsNamesAndLatLongs){
                                FriendsListItem(friendNameAndLatLong = it, onLocateClick = {
                                    onLocateMeClick(it)
                                })
                            }
                        }
                    }
                }


            }

        }
    }
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsListItem(
    friendNameAndLatLong: MyLatLng,
    onLocateClick:(MyLatLng)-> Unit
){
    Row(modifier = Modifier.fillMaxWidth()){
        Box(modifier = Modifier.size(44.dp).clip(
            CircleShape
        )){
            Image(
                painterResource(Res.drawable.ic_avatar),
                contentDescription = null,
                modifier = Modifier.matchParentSize())
        }
        Column(modifier = Modifier.weight(1f).padding(
            horizontal = 10.dp
        ).padding(
            vertical = 5.dp
        )){
            AppText(text = friendNameAndLatLong.senderName.toString(),
                font = Res.font.inter_semibold,
                fontSize = 14.sp,)
        }
        Box(modifier = Modifier.align(
            Alignment.CenterVertically
        ).clip(
            shape = RoundedCornerShape(6.dp)
        ).background(
            color = Color(0xFF3268F0)
        ).clickable(indication = null,
            interactionSource = remember { MutableInteractionSource() },onClick = {
                onLocateClick(friendNameAndLatLong)
            })){
            AppText(text = "locate",
                color = Color.White,
                fontSize = 12.sp,
                font= Res.font.inter_medium,
                modifier = Modifier.padding(
                    8.dp
                ))
        }
    }

}
@Preview
@Composable
fun RealtimeTrackerScreenPrev(){
    AddFriendsBtmSheet(friendsNamesAndLatLongs = listOf(MyLatLng(
        receiverName = "adadad",
        latitude = 33.433455,
        longitude = 73.34335
    ),
        MyLatLng(
            receiverName = "sfweww",
            latitude = 33.433455,
            longitude = 73.34335
        ),
        MyLatLng(
            receiverName = "rwerwrr",
            latitude = 33.433455,
            longitude = 73.34335
        )), onLocateMeClick = {

    }, onAddFriendClick = {

    })
}
//}

//@Preview
//@Composable
//fun RealtimeTrackerScreenPrev(){
//    RealtimeTrackerScreen(rememberNavController(),
//        RealtimeTrackerViewModel())
//}