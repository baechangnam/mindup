package apps.kr.mentalgrowth.ui.login


import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.viewModelScope
import apps.kr.mentalgrowth.model.ApiRequestModel
import apps.kr.mentalgrowth.model.ApiResponseModel
import apps.kr.mentalgrowth.network.NetworkClient.apiService


import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState


    private val _boardList = MutableStateFlow<List<ApiResponseModel.Board>>(emptyList())
    val getBoardList: StateFlow<List<ApiResponseModel.Board>> = _boardList

    private val file01 = MutableStateFlow("")
    val _file01: MutableStateFlow<String> = file01

    private val file02 = MutableStateFlow("")
    val _file02: MutableStateFlow<String> = file02

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun loginWithApi(mem_id: String, mem_pw: String) {
        viewModelScope.launch {
            try {
                val request = ApiRequestModel.LoginRequest(mem_id, mem_pw)
                val response = apiService.login(request)
                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        if (loginResponse.flag.flag == "1") {
                            // 서버 인증 성공 시 UI 상태에 서버 토큰 및 사용자 정보 업데이트
                            _uiState.value = _uiState.value.copy(
                                isLoggedIn = true,
                                mem_id = mem_id,
                                mem_level = loginResponse.flag.mem_level,
                                class_group_id = loginResponse.flag.class_group_id,
                                nickname  = loginResponse.flag.mem_phone,

                                // 필요에 따라 추가 필드 업데이트
                                errorMessage = null
                            )
                            // 추가적으로 서버 토큰을 앱 내 저장소에 보관할 수 있습니다.
                        } else {
                            _uiState.value = _uiState.value.copy(
                                isLoggedIn = false,
                                errorMessage = loginResponse.flag.message
                            )
                        }
                    } ?: run {
                        _uiState.value = _uiState.value.copy(
                            isLoggedIn = false,
                            errorMessage = "빈 응답"
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoggedIn = false,
                        errorMessage = "HTTP 오류: ${response.code()}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoggedIn = false,
                    errorMessage = "예외 발생: ${e.message}"
                )
            }
        }
    }

    fun fetchBoardList(pid : String, keyword : String) {
        viewModelScope.launch {
            try {
                val response = apiService.getBoardList(pid,keyword,"")
                if (response.isSuccessful) {
                    response.body()?.let { list ->
                        _boardList.value = list.boardList
                        _file01.value = list.boardList[0].filename?: ""
                        _file02.value = list.boardList[0].filename2?: ""
                    } ?: run {

                    }
                } else {
                    Log.d("myLog", "11 " +" HTTP 오류: ${response.code()}")

                }
            } catch (e: Exception) {
                Log.d("myLog", "11 " +" 예외 오류: ${e.toString()}")
            }


        }
    }

}

