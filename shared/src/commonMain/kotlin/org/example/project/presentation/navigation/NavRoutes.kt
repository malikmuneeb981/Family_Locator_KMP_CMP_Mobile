package org.example.project.presentation.navigation

sealed class NavRoutes(val route: String) {
    data object SplashScreen: NavRoutes("SplashScreen")
    data object OnBoardingScreen: NavRoutes("OnBoardingScreen")
    data object CreateProfileScreen: NavRoutes("CreateProfileScreen")
    data object HomeScreen: NavRoutes("HomeScreen")
    data object RealtimeTrackerScreen: NavRoutes("RealtimeTrackerScreen")
    data object AddFriendScreen: NavRoutes("AddFriendScreen")
    data object ShowMyQRScreen: NavRoutes("ShowMyQRScreen")
    data object ScanQRScreen: NavRoutes("ScanQRScreen")
}