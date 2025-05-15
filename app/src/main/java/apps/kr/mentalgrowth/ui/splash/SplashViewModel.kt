// SplashViewModel.kt
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import apps.kr.mentalgrowth.common.CommonUtil
import apps.kr.mentalgrowth.model.ApiRequestModel
import apps.kr.mentalgrowth.network.NetworkClient
import apps.kr.mentalgrowth.ui.splash.SplashUiState

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val uiState: StateFlow<SplashUiState> = _uiState
    private val context = getApplication<Application>().applicationContext
    private val sharedPref = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)

    init {
        checkVersion()
    }

    private fun checkVersion() {
        viewModelScope.launch {
            // 네트워크 체크 (CommonUtil은 object로 구현되어 있음)
            val networkOk = CommonUtil.isNetworkAvailable(getApplication())
            if (!networkOk) {
                _uiState.value = SplashUiState.NetworkError
                return@launch
            }

            val currentVersion = CommonUtil.getCurrentAppVersion(getApplication())

            // Retrofit과 코루틴으로 버전 체크 (IO 스레드 사용)
//            val versionOk = withContext(Dispatchers.IO) {
//                NetworkClient.checkAppVersion(currentVersion)
//            }
//
//            if (!versionOk) {
//                _uiState.value = SplashUiState.VersionError
//                return@launch
//            }

            delay(1500L)


            val memId = sharedPref.getString("mem_id", null)

            if (memId.isNullOrEmpty()) {
                _uiState.value = SplashUiState.NavigateToLogin
            } else {
                _uiState.value = SplashUiState.NavigateToMain
              //  checkUserStatus(memId)
            }
        }
    }

    private fun checkUserStatus(memId: String) {
        viewModelScope.launch {
            try {
                val request = ApiRequestModel.LoginRequest("", memId)
                val response = NetworkClient.apiService.checkLogin(request)
                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        if (loginResponse.flag.flag == "1") {
                            // 서버 인증 성공 시 UI 상태에 서버 토큰 및 사용자 정보 업데이트
                            _uiState.value = SplashUiState.NavigateToMain
                            // 추가적으로 서버 토큰을 앱 내 저장소에 보관할 수 있습니다.
                        } else {
                            _uiState.value = SplashUiState.NavigateToLogin
                        }
                    } ?: run {
                        _uiState.value = SplashUiState.NavigateToLogin
                    }
                } else {
                    _uiState.value = SplashUiState.NavigateToLogin

                }
            } catch (e: Exception) {
                _uiState.value = SplashUiState.NetworkError
            }
        }



    }
}


