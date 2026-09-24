package org.example.project.presentation.screens.home

data class HomeScreenUiState(
    val locationPermissionsGranted: Boolean = false,
    val locationPermissionDialogShown: Boolean = false,
    val locationEnabled: Boolean = false,
    val showLocationEnabledDialog: Boolean = false

)
sealed interface HomeScreenUiEvents{
    data object RealTimeTrackerClick: HomeScreenUiEvents
    data object PermissionGranted: HomeScreenUiEvents
    data object AddYourFriendsClick: HomeScreenUiEvents
    data object GoToEnableLocation: HomeScreenUiEvents
    data object DismissLocationEnabledDialog: HomeScreenUiEvents

    data object CheckLocationEnabledStatusOnStartUp: HomeScreenUiEvents

}
sealed interface HomeScreenUiEffects{

    data object NavigateToRealTimeTracker: HomeScreenUiEffects
    data object NavigateToAddYourFriends: HomeScreenUiEffects
}
