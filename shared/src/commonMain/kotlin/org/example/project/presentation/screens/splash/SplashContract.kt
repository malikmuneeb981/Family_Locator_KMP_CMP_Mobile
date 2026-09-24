package org.example.project.presentation.screens.splash

data class SplashUiState(
    val isLoading: Boolean = false,
    val userAuthenticated: Boolean = false
)
sealed interface SplashUiEvents{
    data object ShowLoading: SplashUiEvents
    data object HideLoading: SplashUiEvents
    data object GetStartedBtnClick: SplashUiEvents
    data class CheckForUserAuth(val userName: String): SplashUiEffects
}
sealed interface SplashUiEffects{
    data object NavigateToOnBoarding: SplashUiEffects
    data object ShowBtn: SplashUiEffects
    data object NavigateToHome: SplashUiEffects
}