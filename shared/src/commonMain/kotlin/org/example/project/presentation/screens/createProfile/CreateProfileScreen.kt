package org.example.project.presentation.screens.createProfile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.translate.speech.to.text.dictionary.instant.voice.translator.presentation.composables.AppText
import familylocatorkmp.shared.generated.resources.Res
import familylocatorkmp.shared.generated.resources.inter_medium
import familylocatorkmp.shared.generated.resources.inter_semibold
import familylocatorkmp.shared.generated.resources.profile_img
import familylocatorkmp.shared.generated.resources.upload_img
import org.example.project.commons.SelectImage
import org.example.project.presentation.composables.AppMainBtn
import org.example.project.presentation.composables.AppSecondaryBtn
import org.example.project.presentation.navigation.NavRoutes
import org.jetbrains.compose.resources.vectorResource

@Composable
fun CreateProfileScreen(
    navController: NavController,
    createProfileScreenViewModel: CreateProfileScreenViewModel
) {
    val uiState by createProfileScreenViewModel.uiState.collectAsStateWithLifecycle()

    // Handle One-Time Side Effects (Navigation, Toasts)
    LaunchedEffect(createProfileScreenViewModel) {
        createProfileScreenViewModel.uiEffect.collect { effect ->
            when (effect) {
                is CreateProfileUiEffect.NavigateToHome -> {
                    navController.navigate(NavRoutes.HomeScreen.route)
                    // navController.navigate(NavRoutes.HomeScreen.route)
                }

                is CreateProfileUiEffect.NavigateAsGuest -> {
                    // navController.navigate(NavRoutes.HomeScreen.route)
                }

                is CreateProfileUiEffect.ShowToast -> {
                    // Toast or SnackBar handler
                }
            }
        }
    }
    if (uiState.showAlreadyHaveAccountBtmSheet){
        AlreadyHaveAccountBtmSheet(
            onDismiss = {
                createProfileScreenViewModel.onEvent(
                    CreateProfileUiEvent.AlreadyHaveAccountBtmSheetDismiss
                )
            },
            userNameVale = uiState.alreadyHaveAccountUserName,
            onUserNameChange = {
                createProfileScreenViewModel.onEvent(
                    CreateProfileUiEvent.AlreadyHaveAccountUserNameChanged(it)
                )
            }, onLogInClick = {
                createProfileScreenViewModel.onEvent(
                    CreateProfileUiEvent.AlreadyHaveAccountUserLogIn(userName = uiState.alreadyHaveAccountUserName)
                )
            }, showLoading = uiState.isLogInLoading
        )
    }

    CreateProfileScreenContent(
        uiState = uiState,
        onEvent = createProfileScreenViewModel::onEvent
    )
}

@Composable
fun CreateProfileScreenContent(
    uiState: CreateProfileUiState,
    onEvent: (CreateProfileUiEvent) -> Unit
) {
    if (uiState.isImagePickerOpened) {
        SelectImage { path, base64String ->
            onEvent(CreateProfileUiEvent.OnImageSelected(path = path, base64 = base64String))
        }
    }

    Scaffold(
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AppText(
                    text = "Create Your Profile",
                    font = Res.font.inter_semibold,
                    fontSize = 22.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp)
                )

                // Profile Image / Avatar
                Box(
                    modifier = Modifier
                        .padding(top = 40.dp)
                        .border(
                            width = 1.dp,
                            color = Color(0xFFCFDDF8),
                            shape = RoundedCornerShape(20.dp)
                        )
                ) {
                    if (uiState.selectedImageUri.isNullOrEmpty()) {
                        Image(
                            vectorResource(Res.drawable.profile_img),
                            contentDescription = "Default Profile",
                            modifier = Modifier
                                .padding(32.dp)
                                .size(86.dp)
                        )
                    } else {
                        AsyncImage(
                            model = uiState.selectedImageUri,
                            contentDescription = "Selected Profile Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .padding(32.dp)
                                .size(86.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }
                }

                // Upload Button
                Row(
                    modifier = Modifier
                        .padding(top = 15.dp)
                        .clip(shape = RoundedCornerShape(30.dp))
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = {
                                onEvent(CreateProfileUiEvent.OnOpenImagePicker)
                            }
                        )
                        .background(
                            color = Color(0xFFF4F6FA),
                            shape = RoundedCornerShape(30.dp)
                        )
                        .padding(
                            vertical = 8.dp,
                            horizontal = 15.dp
                        ),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        vectorResource(Res.drawable.upload_img),
                        contentDescription = null
                    )
                    AppText(
                        text = "Upload photo",
                        fontSize = 14.sp,
                        font = Res.font.inter_medium
                    )
                }

                // Username Input
                OutlinedTextField(
                    value = uiState.userName,
                    onValueChange = { onEvent(CreateProfileUiEvent.OnUserNameChanged(it)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 40.dp),
                    label = { AppText(text = "Username") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF3268F0),
                        unfocusedBorderColor = Color(0xFFD9D9D9),
                        unfocusedLabelColor = Color(0xFF7D7D8C),
                        focusedLabelColor = Color(0xFF7D7D8C)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                // Phone Number Input
                OutlinedTextField(
                    value = uiState.phoneNumber,
                    onValueChange = { onEvent(CreateProfileUiEvent.OnPhoneNumberChanged(it)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 20.dp),
                    label = { AppText(text = "Phone Number") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF3268F0),
                        unfocusedBorderColor = Color(0xFFD9D9D9),
                        unfocusedLabelColor = Color(0xFF7D7D8C),
                        focusedLabelColor = Color(0xFF7D7D8C)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                // Error Message Display
//                if (uiState.errorMessage != null) {
//                    AppText(
//                        text = uiState.errorMessage,
//                        color = Color.Red,
//                        fontSize = 13.sp,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(horizontal = 20.dp, vertical = 8.dp)
//                    )
//                }

                Spacer(modifier = Modifier.weight(1f))

                // Loading or Sign Up Button
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(bottom = 20.dp),
                        color = Color(0xFF3268F0)
                    )
                } else {
                    AppMainBtn(
                        text = "Sign Up",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .padding(bottom = 20.dp),
                        onClick = {
                            onEvent(CreateProfileUiEvent.OnSignUpClicked)
                        }
                    )
                }

                // Guest User Button
                AppSecondaryBtn(
                    text = "Log In",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 30.dp),
                    onClick = {
                        onEvent(CreateProfileUiEvent.OnAlreadyHaveAccountClicked)
                    }
                )
            }
        },
        containerColor = Color.White
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlreadyHaveAccountBtmSheet(showLoading: Boolean,
                               onDismiss:()-> Unit,
                               userNameVale: String,
                               onUserNameChange:(String)-> Unit,
                               onLogInClick:()-> Unit){
    val btmSheetState = rememberModalBottomSheetState()
    ModalBottomSheet(sheetState = btmSheetState, onDismissRequest = {
        onDismiss()
    }, containerColor = Color.White){
        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally){
            OutlinedTextField(
                value = userNameVale,
                onValueChange = {
                    onUserNameChange(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    ,
                label = { AppText(text = "Username") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF3268F0),
                    unfocusedBorderColor = Color(0xFFD9D9D9),
                    unfocusedLabelColor = Color(0xFF7D7D8C),
                    focusedLabelColor = Color(0xFF7D7D8C)
                ),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )
            Box(modifier = Modifier.fillMaxWidth().padding(
                top = 20.dp,
            ), contentAlignment = Alignment.Center){
                if (showLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(bottom = 20.dp),
                        color = Color(0xFF3268F0)
                    )
                } else {
                    AppMainBtn(text = "Continue", modifier = Modifier.fillMaxWidth().padding(
                        horizontal = 20.dp
                    ),onClick = {
                        onLogInClick()
                    })
                }
            }


        }
    }
}
@Preview
@Composable
fun AlreadyHaveAccountBtmSheetPreview() {
    AlreadyHaveAccountBtmSheet(
       onDismiss = {

       }, userNameVale = "",
        onUserNameChange = {

        }, onLogInClick = {

        }, showLoading = true
    )
}

@Preview
@Composable
fun CreateProfileScreenPreview() {
    CreateProfileScreenContent(
        uiState = CreateProfileUiState(userName = "John Doe", phoneNumber = "+1234567890"),
        onEvent = {}
    )
}
