package apps.kr.mentalgrowth.ui.main.viewmodel


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import apps.kr.mentalgrowth.model.ApiRequestModel
import apps.kr.mentalgrowth.model.ApiResponseModel
import apps.kr.mentalgrowth.model.CommonUiState
import apps.kr.mentalgrowth.network.NetworkClient
import apps.kr.mentalgrowth.network.NetworkClient.apiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel() : ViewModel() {
    private val _getBoardList = MutableStateFlow<List<ApiResponseModel.Board>>(emptyList())
    val getBoardList: StateFlow<List<ApiResponseModel.Board>> = _getBoardList

    private val _getNoticeList = MutableStateFlow<List<ApiResponseModel.Board>>(emptyList())
    val getNoticeList: StateFlow<List<ApiResponseModel.Board>> = _getNoticeList

    private val _getBoardVideo = MutableStateFlow<List<ApiResponseModel.Board>>(emptyList())
    val getBoardVideo: StateFlow<List<ApiResponseModel.Board>> = _getBoardVideo


    private val _getBoardTitle = MutableStateFlow("")
    val getBoardTitle: MutableStateFlow<String> = _getBoardTitle

    private val _getCommCodeDesc = MutableStateFlow("")
    val commCodeDesc: MutableStateFlow<String> = _getCommCodeDesc

    private val _getBoardIdx = MutableStateFlow("")
    val getBoardIdx: MutableStateFlow<String> = _getBoardIdx


    private val _getRankList = MutableStateFlow<List<ApiResponseModel.Rank>>(emptyList())
    val getRankList: StateFlow<List<ApiResponseModel.Rank>> = _getRankList

    private val rankTitle = MutableStateFlow("")
    val myRankTitle: MutableStateFlow<String> = rankTitle

    private val myTeir = MutableStateFlow("")
    val _myTeir: MutableStateFlow<String> = myTeir
    private val myLevel = MutableStateFlow("")
    val _myLevel: MutableStateFlow<String> = myLevel

    private val _uiState = MutableStateFlow(CommonUiState())
    val uiState: StateFlow<CommonUiState> = _uiState


    private val _board = MutableStateFlow<ApiResponseModel.Board?>(null)
    val board: MutableStateFlow<ApiResponseModel.Board?> = _board

    private val _commentList = MutableStateFlow<List<ApiResponseModel.Comment>>(emptyList())
    val commentList: MutableStateFlow<List<ApiResponseModel.Comment>> = _commentList

    private val _hearList = MutableStateFlow<List<ApiResponseModel.HeartDay>>(emptyList())
    val hearList: MutableStateFlow<List<ApiResponseModel.HeartDay>> = _hearList


    private val _isWrite = MutableStateFlow("")
    val isWrite: MutableStateFlow<String> = _isWrite


    private val _isDelete = MutableStateFlow("0")
    val isDelete: MutableStateFlow<String> = _isDelete

    private val _isComment = MutableStateFlow("0")
    val isComment: MutableStateFlow<String> = _isComment

    private val _hCnt = MutableStateFlow("0")
    val hCount: MutableStateFlow<String> = _hCnt

    private val _eCnt = MutableStateFlow("0")
    val eCount: MutableStateFlow<String> = _eCnt

    private val _aCnt = MutableStateFlow("0")
    val aCount: MutableStateFlow<String> = _aCnt

    private val _rCnt = MutableStateFlow("0")
    val rCount: MutableStateFlow<String> = _rCnt

    private val _tCnt = MutableStateFlow("0")
    val tCount: MutableStateFlow<String> = _tCnt


    private val _isUpdate = MutableStateFlow("0")
    val isUpdate: MutableStateFlow<String> = _isUpdate

    private val _detailDescription = MutableStateFlow<String?>(null)
    val detailDescription: StateFlow<String?> = _detailDescription

    // ② 코드로 API 콜
    fun fetchDetail(code: String) {
        viewModelScope.launch {
            try {
                // 예시) NetworkClient.api.getDetail(code) → { title, description }
               // val response = apiService.getDetail(code)
                //_detailDescription.value = response.description
            } catch (e: Exception) {
                _detailDescription.value = "설명을 불러오는 중 오류가 발생했습니다."
            }
        }
    }


    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = "")
        _uiState.value = _uiState.value.copy(flag = "0")
    }

    private val _memList = MutableStateFlow<List<ApiResponseModel.MemberInfo>>(emptyList())
    val memList: MutableStateFlow<List<ApiResponseModel.MemberInfo>> = _memList

    private val _groupList = MutableStateFlow<List<ApiResponseModel.MemberInfo>>(emptyList())
    val groupList: MutableStateFlow<List<ApiResponseModel.MemberInfo>> = _groupList

    private val _golfAnnualList = MutableStateFlow<List<ApiResponseModel.MonthSummary>>(emptyList())
    val getAnnualList: StateFlow<List<ApiResponseModel.MonthSummary>> = _golfAnnualList

    fun getMyInfo(mem_id: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getMyInfo(mem_id)
                if (response.isSuccessful) {
                    response.body()?.let { list ->
                        memList.value = list.boardList
                    } ?: run {

                    }
                } else {
                    Log.d("myLog", "11 " + " HTTP 오류: ${response.code()}")

                }
            } catch (e: Exception) {
                Log.d("myLog", "11 " + " 예외 오류: ${e.toString()}")
            }


        }
    }


    fun getMemInfo(mem_id: String,group_id:String) {
        viewModelScope.launch {
            try {
                val response = apiService.getMember(mem_id,group_id)
                if (response.isSuccessful) {
                    response.body()?.let { list ->
                        _groupList.value = list.boardList
                    } ?: run {

                    }
                } else {
                    Log.d("myLog", "11 " + " HTTP 오류: ${response.code()}")

                }
            } catch (e: Exception) {
                Log.d("myLog", "11 " + " 예외 오류: ${e.toString()}")
            }


        }
    }

    fun getBoard(pid: String, groupId: String, noti_flag: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getBoardList(pid, groupId, noti_flag)
                if (response.isSuccessful) {
                    response.body()?.let { list ->
                        if (pid == "1") {
                            _getBoardList.value = list.boardList
                        } else {
                            if (noti_flag == "1") {
                                getBoardTitle.value = list.boardList[0].title
                            } else {
                                _getBoardVideo.value = list.boardList
                            }

                        }

                    } ?: run {

                    }
                } else {
                    Log.d("myLog", "11 " + " HTTP 오류: ${response.code()}")

                }
            } catch (e: Exception) {
                Log.d("myLog", "11 " + " 예외 오류: ${e.toString()}")
            }


        }
    }

    fun getRank(mem_id: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getRank(mem_id)
                if (response.isSuccessful) {
                    response.body()?.let { list ->
                        _getRankList.value = list.board_list
                        rankTitle.value = list.rank.toString()

                    } ?: run {

                    }
                } else {
                    Log.d("myLog", "11 " + " HTTP 오류: ${response.code()}")

                }
            } catch (e: Exception) {
                Log.d("myLog", "11 " + " 예외 오류: ${e.toString()}")
            }


        }
    }

    fun getRankMy(mem_id: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getRankMy(mem_id)
                if (response.isSuccessful) {
                    response.body()?.let { list ->

                        myTeir.value = list.data.tier
                        myLevel.value = list.data.level

                    } ?: run {

                    }
                } else {
                    Log.d("myLog", "11 " + " HTTP 오류: ${response.code()}")

                }
            } catch (e: Exception) {
                Log.d("myLog", "11 " + " 예외 오류: ${e.toString()}")
            }


        }
    }

    fun getCal(mem_id: String,yyyy: String,mm: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getCal(mem_id,yyyy,mm)
                if (response.isSuccessful) {
                    response.body()?.let { list ->

                        _hearList.value = list.board_list

                    } ?: run {

                    }
                } else {
                    Log.d("myLog", "11 " + " HTTP 오류: ${response.code()}")

                }
            } catch (e: Exception) {
                Log.d("myLog", "11 " + " 예외 오류: ${e.toString()}")
            }


        }
    }

    fun getBoardTouch(pid: String, groupId: String, code: String, reg_id: String, tag: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getBoardListTouch(pid, groupId, code,reg_id,tag)
                if (response.isSuccessful) {
                    response.body()?.let { list ->
                        _getBoardList.value = list.boardList

                    } ?: run {

                    }
                } else {
                    Log.d("myLog", "11 " + " HTTP 오류: ${response.code()}")

                }
            } catch (e: Exception) {
                Log.d("myLog", "11 " + " 예외 오류: ${e.toString()}")
            }


        }
    }

    fun getBoardChal(pid: String, groupId: String, code: String, reg_id: String, tag: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getBoardListTouch(pid, groupId, code,reg_id,tag)
                if (response.isSuccessful) {
                    response.body()?.let { list ->
                        _getNoticeList.value = list.boardList

                    } ?: run {

                    }
                } else {
                    Log.d("myLog", "11 " + " HTTP 오류: ${response.code()}")

                }
            } catch (e: Exception) {
                Log.d("myLog", "11 " + " 예외 오류: ${e.toString()}")
            }


        }
    }

    fun getBoardTalk(pid: String, groupId: String, code: String, reg_id: String, tag: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getBoardListTalk(pid, groupId, code,reg_id,tag)
                if (response.isSuccessful) {
                    response.body()?.let { list ->
                        _getBoardList.value = list.boardList

                    } ?: run {

                    }
                } else {
                    Log.d("myLog", "11 " + " HTTP 오류: ${response.code()}")

                }
            } catch (e: Exception) {
                Log.d("myLog", "11 " + " 예외 오류: ${e.toString()}")
            }


        }
    }

    fun getBoardListTouchTotal(pid: String, groupId: String, code: String, reg_id: String, tag: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getBoardListTouchTotal(pid, groupId, code,reg_id,tag)
                if (response.isSuccessful) {
                    response.body()?.let { list ->
                        hCount.value= list.count_info.h_cnt?:"0"
                        eCount.value= list.count_info.e_cnt?:"0"
                        aCount.value= list.count_info.a_cnt?:"0"
                        rCount.value= list.count_info.r_cnt?:"0"
                        tCount.value= list.count_info.t_cnt?:"0"

                    } ?: run {

                    }
                } else {
                    Log.d("myLog", "11 " + " HTTP 오류: ${response.code()}")

                }
            } catch (e: Exception) {
                Log.d("myLog", "11 " + " 예외 오류: ${e.toString()}")
            }


        }
    }

    fun getNotice(pid: String, groupId: String, noti_flag: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getBoardList(pid, groupId, noti_flag)
                if (response.isSuccessful) {
                    response.body()?.let { list ->

                        _getNoticeList.value = list.boardList

                    } ?: run {

                    }
                } else {
                    Log.d("myLog", "11 " + " HTTP 오류: ${response.code()}")

                }
            } catch (e: Exception) {
                Log.d("myLog", "11 " + " 예외 오류: ${e.toString()}")
            }


        }
    }
    fun fetchBoardDetail(idx : String) {
        viewModelScope.launch {
            try {
                val response = apiService.getBoardDetail(idx)
                if (response.isSuccessful) {
                    response.body()?.let { list ->
                        board.value = list.boardList[0]
                        commentList.value = list.comment_list
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

    fun fetchBoardDetailTouch(idx : String,memId: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getBoardDetail(idx,memId)
                if (response.isSuccessful) {
                    response.body()?.let { list ->
                        board.value = list.boardList[0]
                        commentList.value = list.comment_list
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

    fun fetchCommonCode(idx : String) {
        viewModelScope.launch {
            try {
                val response = apiService.getCommCode(idx)
                if (response.isSuccessful) {
                    response.body()?.let { list ->
                        _getCommCodeDesc.value = list.flag.flag
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

    fun updateComment(courseId: String, contents: String) {
        viewModelScope.launch {
            try {
                val response = apiService.updateComment(courseId,contents)
                if (response.isSuccessful) {
                    response.body()?.let { list ->
                        isUpdate.value = list.flag.flag
                    } ?: run {

                    }
                    // 삭제 성공 시, 최신 리스트 재요청 (regId 전달)

                } else {
                    Log.d("myLog", "삭제 HTTP 오류: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.d("myLog", "삭제 예외 오류: ${e}")
            }
        }
    }

    fun regNote(title: String, idx: String, mem_id: String,shortMemo :String) {
        viewModelScope.launch {
            try {
                val response = apiService.regNote(title,idx,mem_id,shortMemo,"")
                if (response.isSuccessful) {
                    response.body()?.let { list ->
                        isUpdate.value = list.flag.flag
                    } ?: run {

                    }
                    // 삭제 성공 시, 최신 리스트 재요청 (regId 전달)

                } else {
                    Log.d("myLog", "삭제 HTTP 오류: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.d("myLog", "삭제 예외 오류: ${e}")
            }
        }
    }



    fun registerComment(
        board_idx : String , mem_id : String , contents : String, pid : String
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {

                val response = apiService.regComment(mem_id,board_idx,contents,pid)
                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        if (loginResponse.flag.flag == "1") {
                            // 서버 인증 성공 시 UI 상태에 서버 토큰 및 사용자 정보 업데이트
                            _uiState.value = _uiState.value.copy(
                                flag = "1",
                                message = "",
                                isLoading = false  // API 완료 후 로딩 false

                            )
                            // 추가적으로 서버 토큰을 앱 내 저장소에 보관할 수 있습니다.
                        } else {
                            _uiState.value = _uiState.value.copy(
                                flag = "0",
                                message = loginResponse.flag.message,
                                isLoading = false  // API 완료 후 로딩 false
                            )
                        }
                    } ?: run {
                        _uiState.value = _uiState.value.copy(
                            flag = "0",
                            message = "빈 응답",
                            isLoading = false  // API 완료 후 로딩 false
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        flag = "0",
                        message = "HTTP 오류: ${response.code()}",
                        isLoading = false  // API 완료 후 로딩 false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    flag = "0",
                    message = "예외 발생: ${e.message}",
                    isLoading = false  // API 완료 후 로딩 false
                )
            }
        }
    }

    fun deleteComment(courseId: String) {
        viewModelScope.launch {
            try {
                val response = apiService.deleteComment(courseId)
                if (response.isSuccessful) {
                    response.body()?.let { list ->
                        isComment.value = list.flag.flag
                    } ?: run {

                    }
                    // 삭제 성공 시, 최신 리스트 재요청 (regId 전달)

                } else {
                    Log.d("myLog", "삭제 HTTP 오류: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.d("myLog", "삭제 예외 오류: ${e}")
            }
        }
    }

    fun deleteBoard(courseId: String) {
        viewModelScope.launch {
            try {
                val response = apiService.deleteBoard(courseId)
                if (response.isSuccessful) {
                    response.body()?.let { list ->
                        isDelete.value = list.flag.flag
                    } ?: run {

                    }
                    // 삭제 성공 시, 최신 리스트 재요청 (regId 전달)

                } else {
                    Log.d("myLog", "삭제 HTTP 오류: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.d("myLog", "삭제 예외 오류: ${e}")
            }
        }
    }

    fun getNoticeTitle(pid: String, groupId: String, noti_flag: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getBoardList(pid, groupId, noti_flag)
                if (response.isSuccessful) {
                    response.body()?.let { list ->
                        getBoardTitle.value = list.boardList[0].title
                        getBoardIdx.value = list.boardList[0].idx.toString()

                    } ?: run {

                    }
                } else {
                    Log.d("myLog", "11 " + " HTTP 오류: ${response.code()}")

                }
            } catch (e: Exception) {
                Log.d("myLog", "11 " + " 예외 오류: ${e.toString()}")
            }


        }
    }

    fun fetchGolfAnnualList(regId: String, year: Int) {
        viewModelScope.launch {
            try {
                val response = apiService.getAnnualList(regId, year)
                if (response.isSuccessful) {
                    response.body()?.let { list ->
                        _golfAnnualList.value = list.boardList
                    } ?: run {

                    }
                } else {
                    Log.d("myLog", "11 " + " HTTP 오류: ${response.code()}")

                }
            } catch (e: Exception) {
                Log.d("myLog", "11 " + " 예외 오류: ${e.toString()}")
            }


        }
    }


    fun registerGolfData(
        regData: ApiRequestModel.RegistrationData
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {

                val response = apiService.regGolfData(regData)
                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        if (loginResponse.flag.flag == "1") {
                            // 서버 인증 성공 시 UI 상태에 서버 토큰 및 사용자 정보 업데이트
                            _uiState.value = _uiState.value.copy(
                                flag = "1",
                                message = "",
                                isLoading = false  // API 완료 후 로딩 false

                            )
                            // 추가적으로 서버 토큰을 앱 내 저장소에 보관할 수 있습니다.
                        } else {
                            _uiState.value = _uiState.value.copy(
                                flag = "0",
                                message = loginResponse.flag.message,
                                isLoading = false  // API 완료 후 로딩 false
                            )
                        }
                    } ?: run {
                        _uiState.value = _uiState.value.copy(
                            flag = "0",
                            message = "빈 응답",
                            isLoading = false  // API 완료 후 로딩 false
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        flag = "0",
                        message = "HTTP 오류: ${response.code()}",
                        isLoading = false  // API 완료 후 로딩 false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    flag = "0",
                    message = "예외 발생: ${e.message}",
                    isLoading = false  // API 완료 후 로딩 false
                )
            }
        }
    }




}


