package apps.kr.mentalgrowth.ui.main

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import apps.kr.mentalgrowth.R
import androidx.lifecycle.viewmodel.compose.viewModel
import apps.kr.mentalgrowth.common.CommonUtil.convertToCalendarMap
import apps.kr.mentalgrowth.common.CommonView.TitleWithHearts
import apps.kr.mentalgrowth.model.ApiResponseModel
import apps.kr.mentalgrowth.network.NetworkClient
import apps.kr.mentalgrowth.ui.main.viewmodel.MainViewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MindGrowthScreen( navController: NavController? = null,
                     viewModel: MainViewModel = viewModel()) {

    val boardList by viewModel.getBoardList.collectAsState()
    val getNoticeList by viewModel.getNoticeList.collectAsState()
    val getBoardTitle by viewModel.getBoardTitle.collectAsState()

    val calendarRawData by viewModel.hearList.collectAsState() // 예: List<HeartDay>
    val currentMonth = remember { mutableStateOf(YearMonth.now()) }

    val _myTeir by viewModel._myTeir.collectAsState()
    val _myLevel by viewModel._myLevel.collectAsState()

    val nickName = LocalContext.current
        .getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        .getString("mem_nick", "닉네임")!!

    val class_group_id = LocalContext.current
        .getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        .getString("class_group_id", "")!!

    val mem_id = LocalContext.current
        .getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        .getString("mem_id", "")!!


    LaunchedEffect(mem_id) {
        viewModel.getMyInfo(mem_id)
        viewModel.getBoardTouch("3", class_group_id,"",mem_id,"")
        viewModel.getBoardChal("4", class_group_id,"",mem_id,"")
        viewModel.getRankMy(mem_id)
        viewModel.getMemInfo(mem_id,class_group_id)
    }
    val groupList by viewModel.groupList.collectAsState()

    val mem_level = LocalContext.current
        .getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        .getString("mem_level", "")!!

    val bottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()
    val memberList by viewModel.memList.collectAsState()

    var selectedMember by remember { mutableStateOf<ApiResponseModel.MemberInfo?>(null) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            bottomSheetState.hide()
        }
    }

    LaunchedEffect(memberList) {
        if (selectedMember == null && memberList.isNotEmpty()) {
            selectedMember = memberList[0] // 초기 상태 (본인 정보)
        }
    }

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent = {
            if (bottomSheetState.isVisible) { // ✅ 조건부 컴포지션으로 깜빡임 방지
                RankingInfoScreen(onClose = {
                    coroutineScope.launch { bottomSheetState.hide() }
                })
            }
        },
        modifier = Modifier.zIndex(-1f) // ✅ 레이아웃 초기 깜빡임 방지용
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    backgroundColor = Color(0xFFF8BBD0),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp), // 기본 TopAppBar 높이
                        contentAlignment = Alignment.Center
                    ) {
                        // ✅ 중앙 타이틀 (하트 포함)
                        TitleWithHearts("마음성장")

                        // 🔹 왼쪽: 뒤로가기 버튼
                        Row(
                            modifier = Modifier.align(Alignment.CenterStart),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { navController?.popBackStack() }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_back_black),
                                    contentDescription = "뒤로가기",
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }

                        // 🔹 오른쪽: 홈 버튼
                        Row(
                            modifier = Modifier.align(Alignment.CenterEnd),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = {
                                navController?.navigate("home") {
                                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = "홈으로",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }

            },

            content = { innerPadding ->
                Column(
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .background(Color(0xFFFFF5F7))
                        .padding(innerPadding)
                        .padding(1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        if (mem_level =="LEV003"){
                            GroupDropdown(
                                groupList = groupList,
                                onSelected = {
                                    // 선택된 GroupInfo 처리
                                    Log.d("SelectedGroup", it.toString())
                                    selectedMember = it // ✅ 상태에 저장
                                    var selId = it.mem_id
                                    viewModel.getMyInfo(selId)
                                    viewModel.getBoardTouch("3", class_group_id,"",selId,"")
                                    viewModel.getBoardChal("4", class_group_id,"",selId,"")
                                    viewModel.getRankMy(selId)

                                }
                            )
                        }

                        if (memberList.isNotEmpty()) {
                            if (selectedMember != null) {
                                ProfileScreen(
                                    member = selectedMember!!, // 이건 안전함. 위에서 null 체크함
                                    boardList = boardList,
                                    getNoticeList = getNoticeList,
                                    navController = navController,
                                    _myTeir = _myTeir,
                                    _myLevel = _myLevel,
                                    onShowRankingInfo = {
                                        coroutineScope.launch { bottomSheetState.show() }
                                    }
                                )
                            } else {
                                // 로딩 중 처리 또는 빈 화면 표시
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("학생 정보를 불러오는 중입니다...", color = Color.Gray)
                                }
                            }
                        }


                    }


                }
            }
        )
    }




}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileScreen(
    member: ApiResponseModel.MemberInfo,
    boardList: List<ApiResponseModel.Board>,
    getNoticeList: List<ApiResponseModel.Board>,
    navController: NavController?,
    _myTeir: String,
    _myLevel: String, onShowRankingInfo: () -> Unit,viewModel: MainViewModel = viewModel()
) {

    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val calendarRawData by viewModel.hearList.collectAsState()
    val mem_id = member.mem_id

// convertToCalendarMap(): 앞서 설명한 함수
    val calendarData = remember(calendarRawData, currentMonth) {
        convertToCalendarMap(calendarRawData, currentMonth)
    }


    val tabs = listOf("나의 활동", "캘린더", "챌린지", "랭킹")
    var selectedTab by remember { mutableStateOf("나의 활동") }

// API 요청
    LaunchedEffect(currentMonth,mem_id) {
        val year = currentMonth.year.toString()
        val month = "%02d".format(currentMonth.monthValue)
        viewModel.getCal(mem_id, year, month)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()

    ) {
        // 프로필 영역
        Column(  modifier = Modifier.fillMaxWidth(),horizontalAlignment = Alignment.CenterHorizontally) {
            val imageUrl = member.mem_img?.takeIf { it.isNotBlank() }?.let {
                NetworkClient.BASE_URL_MEMBER + it
            }

            AsyncImage(
                model = imageUrl ?: R.drawable.thumbnail_no_img1,
                contentDescription = "프로필 이미지",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(4.dp))
            val shortSchool = member.school.substringBefore("초") + "초"
            Text("${member.mem_phone} (${shortSchool})", fontWeight = FontWeight.Bold, fontSize = 18.sp)

            Spacer(modifier = Modifier.height(4.dp))
            Text("나의 글: ${member.board_cnt?:"0"}개   나의 댓글: ${member.comment_cnt?:"0"}개", fontSize = 13.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(4.dp))
            OutlinedButton(onClick = { navController?.navigate("profile") }, shape = RoundedCornerShape(50)) {
                Text("프로필 수정",  style = MaterialTheme.typography.body2)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 탭 메뉴
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            tabs.forEach { tab ->
                Button(
                    onClick = { selectedTab = tab },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (selectedTab == tab) Color(0xFFFFB6C1) else Color.White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(tab,   style = MaterialTheme.typography.body2, color = if (selectedTab == tab) Color.White else Color.Black)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (selectedTab) {
            "나의 활동" -> {
                MyActivityView(boardList,navController)
            }
            "캘린더" -> {


                HeartCalendarView(
                    currentMonth = currentMonth,
                    calendarData = calendarData,
                    onPrevClick = { currentMonth = currentMonth.minusMonths(1) },
                    onNextClick = { currentMonth = currentMonth.plusMonths(1) }
                )



            }
            "챌린지" -> {
                MyChallengeView(getNoticeList,   navController)
            }
            "랭킹" -> {
                 ChallengeTierAndCommentLevel(_myTeir,_myLevel,onShowRankingInfo)
            }
        }
    }
}

@Composable
fun MyActivityView(boardList: List<ApiResponseModel.Board>, navController: NavController?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {

        boardList.forEach { board ->
            Text(
                text = board.title ?: "(제목 없음)",
                fontSize = 13.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val idx = board.idx ?: return@clickable
                        navController?.navigate("board_detail_touch/touch/${idx}")
                    }
                    .padding(vertical = 8.dp)
            )

            Divider(color = Color(0xFFFFDDE3), thickness = 1.dp)
        }
    }
}


@Composable
fun MyChallengeView(items: List<ApiResponseModel.Board>, navController: NavController?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {

        items.take(5).chunked(2).forEach { rowItems ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                rowItems.forEach { item ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xfffee3ea))
                            .clickable {
                                navController?.navigate("board_detail_chal/touch/${item.idx}")
//                                if (item == null) {
//                                    navController?.navigate("board_reg_pic/${class_group_id}?idx=")
//
//                                } else {
//                                    navController?.navigate("board_detail_picture/touch/${item.idx}")
//                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (item == null) {
                            Icon(
                                painter = painterResource(id = R.drawable.plus2),
                                contentDescription = "등록하기",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(2.dp)
                            ) {
                                val imageUrl = item.filename?.takeIf { it.isNotBlank() }?.let {
                                    NetworkClient.BASE_URL_UPLOAD + it
                                }

                                if (imageUrl != null) {
                                    AsyncImage(
                                        model = imageUrl,
                                        contentDescription = "게시글 이미지",
                                        contentScale = ContentScale.FillBounds,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(8.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                    )
                                } else {
                                    // fallback: 이미지 없을 경우 텍스트 출력
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                        modifier = Modifier.padding(12.dp)
                                    ) {
                                        Text(
                                            text = "이미지 없음",
                                            fontSize = 14.sp,
                                            color = Color.Black
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 열이 3개 안 될 때 빈 공간 추가
                repeat(2 - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun TierImage(_myTeir: String) {
    // 매칭되는 리소스가 없으면 null
    val imageRes: Int? = when (_myTeir) {
        "브론즈 하트"    -> R.drawable.bronze
        "실버 하트"     -> R.drawable.silver
        "골드 하트"     -> R.drawable.gold
        "플래티넘 하트" -> R.drawable.plt
        "다이아 하트"   -> R.drawable.diamond1
        else             -> null
    }

    imageRes?.let { res ->
        Image(
            painter = painterResource(id = res),
            contentDescription = _myTeir,
            modifier = Modifier.size(18.dp)  // 원하는 크기로 조절
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChallengeTierAndCommentLevel(_myTeir: String, _myLevel: String,  onShowRankingInfo: () -> Unit) {
    val (imageRes, tierLabel) = when (_myTeir) {
        "브론즈 하트" -> R.drawable.bronze to "브론즈 하트"
        "실버 하트" -> R.drawable.silver to "실버 하트"
        "골드 하트" -> R.drawable.gold to "골드 하트"
        "플래티넘 하트" -> R.drawable.plt to "플래티넘 하트"
        "다이아 하트" -> R.drawable.diamond1 to "다이아 하트"
        else -> R.drawable.bronze to "없음"
    }

    val (imageRess, levelLabel) = when (_myLevel) {
        "레벨 1" -> R.drawable.lv01 to "레벨 1"
        "레벨 2" -> R.drawable.lv02 to "레벨 2"
        "레벨 3" -> R.drawable.lv03 to "레벨 3"
        "레벨 4" -> R.drawable.lv04 to "레벨 4"
        "레벨 5" -> R.drawable.lv05 to "레벨 5"
        else -> R.drawable.lv01 to "없음"
    }


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 상단 안내 문구
            Text(
                text = "마음 챌린지 참여에 따른 티어와\n나의 댓글 참여에 따른 레벨을 확인할 수 있어요!",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 티어 & 레벨 카드
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // 챌린지 티어 카드
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(Color(0xFFFFF5CC), shape = RoundedCornerShape(20.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = imageRes),
                            contentDescription = tierLabel,
                            modifier = Modifier.size(60.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(tierLabel, fontWeight = FontWeight.Bold)
                    Text("챌린지 티어", fontSize = 13.sp, color = Color.Gray)
                }

                // 댓글 레벨 카드
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(Color(0xFFE0F7FA), shape = RoundedCornerShape(20.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = imageRess),
                            contentDescription = levelLabel,
                            modifier = Modifier.size(60.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(levelLabel, fontWeight = FontWeight.Bold)
                    Text("댓글 레벨", fontSize = 13.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 하단 버튼 또는 안내
            // 하단 버튼 또는 안내 - 클릭 시 Dialog 열기
            Box(
                modifier = Modifier
                    .clickable {   onShowRankingInfo()  }
                    .background(Color(0xFFFFEBEE), shape = RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    "티어 및 레벨 기준",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Red
                )
            }
        }


}


@Composable
fun RankingInfoScreen(onClose: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF0F5))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(16.dp))
                .border(2.dp, Color(0xFFFFB6C1), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "닫기")
                    }
                }

                Row(modifier = Modifier.fillMaxWidth()) {
                    // 왼쪽: 챌린지 티어
                    Column(modifier = Modifier.weight(1f)) {
                        Text("마음 챌린지 티어 기준", fontWeight = FontWeight.Bold, color = Color(0xFFE91E63))
                        Spacer(modifier = Modifier.height(8.dp))

                        val tierList = listOf(
                            "챌린지 1~2개" to R.drawable.bronze,
                            "챌린지 3~5개" to R.drawable.silver,
                            "챌린지 6~9개" to R.drawable.gold,
                            "챌린지 10~14개" to R.drawable.plt,
                            "챌린지 15개 이상" to R.drawable.diamond1
                        )

                        tierList.forEach { (label, imageRes) ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(id = imageRes),
                                    contentDescription = label,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(label, fontSize = 13.sp)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // 오른쪽: 댓글 레벨
                    Column(modifier = Modifier.weight(1f)) {
                        Text("댓글 레벨 기준", fontWeight = FontWeight.Bold, color = Color(0xFFFF9800))
                        Spacer(modifier = Modifier.height(8.dp))

                        val levelList = listOf(
                            "댓글 1~10개" to R.drawable.lv01,
                            "댓글 11~30개" to R.drawable.lv02,
                            "댓글 31~60개" to R.drawable.lv03,
                            "댓글 61~99개" to R.drawable.lv04,
                            "댓글 100개 이상" to R.drawable.lv05
                        )

                        levelList.forEach { (label, imageRes) ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(id = imageRes),
                                    contentDescription = label,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(label, fontSize = 13.sp)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
                }
            }
        }
    }
}

enum class HeartType(val color: Color, val label: String) {
    ATTEND(Color(0xFFFF6B6B), "마음출석"),      // 빨강
    TOUCH(Color(0xFFFFC107), "마음터치"),      // 주황
    CHALLENGE(Color(0xFF81C784), "마음챌린지"), // 연두
    TALK(Color(0xFF64B5F6), "마음톡톡")         // 하늘
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HeartCalendarView(
    currentMonth: YearMonth,
    calendarData: Map<Int, List<HeartType>>,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("yyyy년 M월")
    val firstDay = currentMonth.atDay(1)
    val startDayOffset = (firstDay.dayOfWeek.value % 7) // 월요일=1... 일요일=7
    val daysInMonth = currentMonth.lengthOfMonth()

    Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
        // 상단
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPrevClick) {
                Text("◀")
            }
            Text(currentMonth.format(formatter), fontWeight = FontWeight.Bold, fontSize = 20.sp)
            IconButton(onClick = onNextClick) {
                Text("▶")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 요일 헤더
        val daysOfWeek = listOf("월", "화", "수", "목", "금", "토", "일")
        Row(Modifier.fillMaxWidth()) {
            daysOfWeek.forEach {
                Text(
                    it,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 날짜 그리드
        val totalCells = startDayOffset + daysInMonth
        val rows = (totalCells + 6) / 7
        var day = 1

        Column {
            repeat(rows) {
                Row(Modifier.fillMaxWidth()) {
                    repeat(7) { col ->
                        val index = it * 7 + col
                        if (index < startDayOffset || day > daysInMonth) {
                            Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                        } else {
                            DayCell(
                                day = day,
                                hearts = calendarData[day] ?: emptyList(),
                                modifier = Modifier.weight(1f)
                            )
                            day++
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 범례
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFEBEE), shape = RoundedCornerShape(8.dp))
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            HeartType.values().forEach { type ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(type.color, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(type.label, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun DayCell(day: Int, hearts: List<HeartType>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .border(0.5.dp, Color.LightGray),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(day.toString(), fontSize = 10.sp, color = Color.Black)
        Spacer(modifier = Modifier.height(4.dp))

        val grid = hearts.take(4).chunked(2)

        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            grid.forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    row.forEach { heart ->
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(heart.color, shape = CircleShape)
                        )
                    }
                    repeat(2 - row.size) {
                        Spacer(modifier = Modifier.size(10.dp))
                    }
                }
            }
        }
    }
}

