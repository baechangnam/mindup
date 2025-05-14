package apps.kr.mentalgrowth.ui.splash

sealed class SplashUiState {
    data object Loading : SplashUiState()
    data object NavigateToLogin : SplashUiState()
    data object NetworkError : SplashUiState()
    data object VersionError : SplashUiState()
    data object WithdrawalUser : SplashUiState()
    data object NavigateToMain : SplashUiState()

}