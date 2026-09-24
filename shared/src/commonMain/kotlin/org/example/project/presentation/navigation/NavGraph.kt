package org.example.project.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.example.project.presentation.screens.addFriend.AddFriendScreen
import org.example.project.presentation.screens.addFriend.AddFriendScreenViewModel
import org.example.project.presentation.screens.createProfile.CreateProfileScreen
import org.example.project.presentation.screens.createProfile.CreateProfileScreenViewModel
import org.example.project.presentation.screens.home.HomeScreen
import org.example.project.presentation.screens.home.HomeScreenViewModel
import org.example.project.presentation.screens.onBoarding.OnBoardingScreen
import org.example.project.presentation.screens.realtimetracker.RealtimeTrackerScreen
import org.example.project.presentation.screens.realtimetracker.RealtimeTrackerViewModel
import org.example.project.presentation.screens.scanQR.ScanQRScreen
import org.example.project.presentation.screens.scanQR.ScanQRScreenViewModel
import org.example.project.presentation.screens.showMyQR.ShowMyQRScreen
import org.example.project.presentation.screens.showMyQR.ShowMyQRViewModel
import org.example.project.presentation.screens.splash.SplashScreen
import org.example.project.presentation.screens.splash.SplashViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NavGraph(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = NavRoutes.SplashScreen.route){
        composable(NavRoutes.SplashScreen.route){
            val splashViewModel = koinViewModel<SplashViewModel>()
            SplashScreen(navController = navController, splashViewModel = splashViewModel)
        }
        composable(NavRoutes.OnBoardingScreen.route){
            OnBoardingScreen(navController = navController)
        }
        composable(NavRoutes.CreateProfileScreen.route){
            val createProfileScreenViewModel = koinViewModel<CreateProfileScreenViewModel>()
            CreateProfileScreen(navController = navController, createProfileScreenViewModel = createProfileScreenViewModel)
        }
        composable(NavRoutes.HomeScreen.route){
            val homeScreenViewModel = koinViewModel<HomeScreenViewModel>()
            HomeScreen(navController = navController, homeScreenViewModel = homeScreenViewModel)
        }
        composable(NavRoutes.RealtimeTrackerScreen.route){
            val realtimeTrackerViewModel = koinViewModel<RealtimeTrackerViewModel>()
            RealtimeTrackerScreen(navController = navController, realtimeTrackerViewModel = realtimeTrackerViewModel)
        }
        composable(NavRoutes.AddFriendScreen.route){
            val addFriendScreenViewModel = koinViewModel<AddFriendScreenViewModel>()
            AddFriendScreen(navController = navController, addFriendScreenViewModel = addFriendScreenViewModel)
        }
        composable(NavRoutes.ShowMyQRScreen.route){
            val showMyQRViewModel = koinViewModel<ShowMyQRViewModel>()
            ShowMyQRScreen(navController = navController, showMyQRViewModel = showMyQRViewModel)
        }
        composable(NavRoutes.ScanQRScreen.route){
            val scanQRScreenViewModel = koinViewModel<ScanQRScreenViewModel>()
            ScanQRScreen(navController = navController, scanQRScreenViewModel = scanQRScreenViewModel)
        }
    }
}