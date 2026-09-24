package org.example.project.presentation.screens.addFriend

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController

import com.translate.speech.to.text.dictionary.instant.voice.translator.presentation.composables.AppText
import familylocatorkmp.shared.generated.resources.Res
import familylocatorkmp.shared.generated.resources.ic_avatar
import familylocatorkmp.shared.generated.resources.ic_copy_code
import familylocatorkmp.shared.generated.resources.ic_showhidetextfield
import familylocatorkmp.shared.generated.resources.ic_showtextfield
import familylocatorkmp.shared.generated.resources.inter_medium
import familylocatorkmp.shared.generated.resources.inter_semibold
import kotlinx.coroutines.launch
import org.example.project.commons.GetCameraPermission
import org.example.project.presentation.composables.nonRippleClick
import org.example.project.domain.models.local.ConnectedFriends
import org.example.project.presentation.composables.AppMainBtn
import org.example.project.presentation.composables.AppSecondaryBtn
import org.example.project.presentation.composables.CustomSnackBar
import org.example.project.presentation.composables.GenericTopBar
import org.example.project.presentation.navigation.NavRoutes
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun AddFriendScreen(
    navController: NavController,
    addFriendScreenViewModel: AddFriendScreenViewModel
){
    val uiState by addFriendScreenViewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    if (uiState.getCameraPermission){
        GetCameraPermission(
            permissionGranted = {
                navController.navigate(NavRoutes.ScanQRScreen.route)
                addFriendScreenViewModel.onEvent(
                    AddFriendUiEvent.CameraPermissionGranted
                )
            }, showPermissionRationale = {
                addFriendScreenViewModel.onEvent(
                    AddFriendUiEvent.ShowPermissionRationale
                )
            }
        )
    }

    LaunchedEffect(addFriendScreenViewModel){
        addFriendScreenViewModel.onEvent(
            AddFriendUiEvent.GetAllConnectedFriendsFromRoom
        )
        addFriendScreenViewModel.uiEffect.collect {
            when(it){
                AddFriendUiEffect.NavigateToShowMyQR -> {
                    navController.navigate(NavRoutes.ShowMyQRScreen.route)
                }

                AddFriendUiEffect.NavigateBack -> {
                    navController.navigate(NavRoutes.HomeScreen.route)
                }

                AddFriendUiEffect.NavigateToRealtimeTracker -> {

                }

                AddFriendUiEffect.UserAddedSuccessfully -> {

                }
                is AddFriendUiEffect.UserNotAddedShowError -> {

                }

                AddFriendUiEffect.UserDeletedSuccessfully -> {

                }
                is AddFriendUiEffect.UserDeletionErrorShowError -> {

                }

                AddFriendUiEffect.NavigateToScanQROnCamPermGranted -> {
                    navController.navigate(NavRoutes.ScanQRScreen.route)
                }

                AddFriendUiEffect.ShowSnackBarTextCopied -> {
                    scope.launch {
                        snackBarHostState.showSnackbar(
                            message = "Text Copied To Clipboard"
                        )
                    }
                }
            }
        }
    }
    Scaffold(
        topBar = {
            GenericTopBar(
                text = "Add a friend",
                showScan = true,
                onBackClick = {
                    addFriendScreenViewModel.onEvent(
                        AddFriendUiEvent.BackBtnClick
                    )
                }, onScanClick = {
                    addFriendScreenViewModel.onEvent(
                        AddFriendUiEvent.ScanQRClick
                    )
                }
            )
        }, snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState
            ) {
                CustomSnackBar(
                    message = it.visuals.message,
                    modifier = Modifier.padding(
                        bottom = 5.dp
                    )
                )
            }
        }, content = {

            Column(modifier = Modifier.fillMaxSize().padding(
                it
            ).padding(
                horizontal = 20.dp
            )) {
                CustomOutlinesTextField(text = uiState.friendsUserName,
                    onValueChange = {
                        addFriendScreenViewModel.onEvent(
                            AddFriendUiEvent.FriendsNameTextChanged(it)
                        )
                    }, isFriendNameTextVisible = uiState.isFriendNameTextVisible,
                    onShowHideClick = {
                        addFriendScreenViewModel.onEvent(
                            AddFriendUiEvent.FriendsTextHideClick(
                                !uiState.isFriendNameTextVisible
                            )
                        )
                    })
                AppMainBtn(
                    text = "Add Now",
                    onClick = {
                        addFriendScreenViewModel.onEvent(
                            AddFriendUiEvent.AddNowBtnClick(
                                userName = uiState.friendsUserName
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth().padding(
                        top = 25.dp
                    )
                )
                AppText(text = "My Invitation Code",
                    font = Res.font.inter_semibold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(
                        top = 35.dp
                    ))
                MyInvitationCode(modifier = Modifier.fillMaxWidth().padding(
                    top = 20.dp
                ), text = uiState.myUserDetails.userName.toString(),
                    onClick = { textToCopy ->
                        addFriendScreenViewModel.onEvent(
                            AddFriendUiEvent.CopyTextToClipBoard(text = textToCopy)
                        )
                    }
                )
                AppSecondaryBtn(text = "Show my QR Code",
                    onClick = {
                        addFriendScreenViewModel.onEvent(
                            AddFriendUiEvent.ShowMyQRBtnClick
                        )
                    }, modifier = Modifier.padding(
                        top = 20.dp
                    ))

                AppText(text = "Connected Friends",
                    font = Res.font.inter_semibold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(
                        top = 20.dp
                    ))
                Box(modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center){
                    if (uiState.connectedUsers.isEmpty()){
                        AppText(modifier = Modifier.fillMaxWidth().padding(
                            horizontal = 20.dp
                        ),
                            text = "No Friends Added Yet Scan Qr or Type Friends Name to Add",
                            font = Res.font.inter_medium,
                            fontSize = 14.sp,
                            color = Color(0xFF7D7D8C))
                    }else{
                        MyFriendsList(modifier = Modifier.fillMaxWidth().matchParentSize(), list = uiState.connectedUsers, onDeleteClick = {
                            addFriendScreenViewModel.onEvent(
                                AddFriendUiEvent.FriendDeleteClicked(connectedFriends = it)
                            )
                        })
                    }
                }

            }
        }, containerColor = Color(0xFFF4F6FA)
    )
}
@Composable
fun MyFriendsList(modifier: Modifier= Modifier, list: List<ConnectedFriends>,
                  onDeleteClick:(ConnectedFriends)-> Unit){
    LazyColumn(modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(15.dp),
        contentPadding = PaddingValues(vertical = 20.dp)) {
        items(list){
            FriendsListItem(friend = it, onDeleteClick = {
                onDeleteClick(it)
            })
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsListItem(
    friend: ConnectedFriends,
    onDeleteClick:(ConnectedFriends)-> Unit
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
            AppText(text = friend.name,
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
                onDeleteClick(friend)
            })){
            AppText(text = "Remove",
                color = Color.White,
                fontSize = 12.sp,
                font= Res.font.inter_medium,
                modifier = Modifier.padding(
                    8.dp
                ))
        }
    }

}
@Composable
fun MyInvitationCode(
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit,
    text: String
) {
    Box(
        modifier = modifier
            .clip(shape = RoundedCornerShape(10.dp))
            .background(
                color = Color(0xFF3268F0).copy(
                    alpha = 0.1f
                )
            )
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = {
                    onClick(text)
                }
            )
            .padding(
                horizontal = 16.dp,
                vertical = 18.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        AppText(
            text = text,
            color = Color(0xFF3268F0),
            font = Res.font.inter_semibold,
            modifier = Modifier.align(Alignment.Center)
        )
        Image(
            vectorResource(Res.drawable.ic_copy_code),
            contentDescription = null,
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }
}
@Composable
fun CustomOutlinesTextField(text: String,
                            onValueChange:(String)-> Unit,
                            isFriendNameTextVisible: Boolean,
                            onShowHideClick:(Boolean)-> Unit){
    OutlinedTextField(
        value = text,
        onValueChange = {
                onValueChange(it)
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        label = { AppText(text = "Enter friend code") },
        visualTransformation = if (
            isFriendNameTextVisible
        ) VisualTransformation.None else PasswordVisualTransformation(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF3268F0),
            unfocusedBorderColor = Color(0xFFD9D9D9),
            unfocusedLabelColor = Color(0xFF7D7D8C),
            focusedLabelColor = Color(0xFF7D7D8C)
        ),
        trailingIcon = {
            Image(
                vectorResource(if (isFriendNameTextVisible) Res.drawable.ic_showtextfield else Res.drawable.ic_showhidetextfield),
                contentDescription = if (isFriendNameTextVisible) "Hide password" else "Show password",
                modifier = Modifier.nonRippleClick {
                    onShowHideClick(!isFriendNameTextVisible)
                }
            )
        }
    )
}
//@Preview
//@Composable
//fun AddFriendScreenPrev(
//){
//    AddFriendScreen(rememberNavController(),
//        AddFriendScreenViewModel
//    ())
//}