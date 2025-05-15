package apps.kr.mentalgrowth.ui.main

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import apps.kr.mentalgrowth.R
import androidx.lifecycle.viewmodel.compose.viewModel
import apps.kr.mentalgrowth.common.CommonView.TitleWithHearts
import apps.kr.mentalgrowth.model.ApiResponseModel
import apps.kr.mentalgrowth.network.NetworkClient
import apps.kr.mentalgrowth.ui.main.viewmodel.MainViewModel
import coil.compose.AsyncImage

//import apps.kr.mentalgrowth.ui.main.viewmodel.GolfCourseListViewModel

@Composable
fun MindTouchScreen( navController: NavController? = null,
                viewModel: MainViewModel = viewModel()) {

    val boardList by viewModel.getBoardList.collectAsState()
    val boardVideoList by viewModel.getBoardVideo.collectAsState()
    val getBoardTitle by viewModel.getBoardTitle.collectAsState()


    val nickName = LocalContext.current
        .getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        .getString("mem_nick", "닉네임")!!

    val class_group_id = LocalContext.current
        .getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        .getString("class_group_id", "")!!

    val mem_id = LocalContext.current
        .getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        .getString("mem_id", "")!!

    val groupList by viewModel.groupList.collectAsState()
    val mem_level = LocalContext.current
        .getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        .getString("mem_level", "")!!

    // ① 선택된 글자 상태
    var selectedLetter by remember { mutableStateOf<String?>(null) }

    // heartData 재사용을 위해 따로 정의
    val heartData = mapOf(
        "H" to listOf("마음 이름", "마음 무지개", "마음 네컷", "마음 날씨"),
        "E" to listOf("심라면", "마음 레시피", "마음 바구니", "마음 플레이"),
        "A" to listOf("마음 이음", "마음 닿음", "마음 화음", "마음 맺음"),
        "R" to listOf("마음 놀이터", "마음 작업소", "마음 서클", "마음 약속"),
        "T" to listOf("마음 신호등", "마음 리포트", "마음 캠프", "마음 119")
    )

    LaunchedEffect(mem_id) {
        viewModel.getBoardTouch("3", class_group_id,"",mem_id,"")
        viewModel.getMemInfo(mem_id,class_group_id)

    }

    val descriptions = listOf("알아봐요", "조절해요", "소통해요", "함께해요", "건강해요")


    Scaffold(
        topBar = {
            // ─── 상단 AppBar 영역 ─────────────────────────
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
                    TitleWithHearts("마음터치")

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
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Spacer(Modifier.height(12.dp))

                if (mem_level =="LEV003"){
                    GroupDropdown(
                        groupList = groupList,
                        onSelected = {
                            // 선택된 GroupInfo 처리
                            Log.d("SelectedGroup", it.toString())
                            var selId = it.mem_id
                            viewModel.getBoardTouch("3", class_group_id,"",selId,"")

                        }
                    )
                }

                val letters = listOf("H","E","A","R","T")
                val descriptions = listOf("알아봐요", "조절해요", "소통해요", "함께해요", "건강해요")
                // ② 상단 HEART Row: 선택된 글자는 분홍, 아니면 회색

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    letters.forEachIndexed { idx, letter ->
                        val desc = descriptions.getOrNull(idx).orEmpty()
                        Box(
                            modifier = Modifier
                                .weight(1f)             // ➊ 동일 비율 분할
                                .aspectRatio(1f)        // ➋ 정사각형 유지
                                .padding(horizontal = 4.dp)
                                .clickable { selectedLetter = letter },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = if (selectedLetter == letter) Color(0xFFFFC0CB) else Color(0xFFEBEBEB),
                                modifier = Modifier.fillMaxSize()
                            )
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(4.dp)
                            ) {
                                Text(
                                    text = letter,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedLetter == letter) Color.Black else Color.DarkGray,
                                    modifier = Modifier.padding(bottom = 1.dp)
                                )
                                Text(
                                    text = desc,
                                    fontSize = 10.sp,
                                    color = if (selectedLetter == letter) Color.Black else Color.DarkGray,
                                    textAlign = TextAlign.Center,
                                    style = LocalTextStyle.current.copy(
                                        lineHeight = 10.sp,
                                        letterSpacing = (-0.5).sp
                                    )
                                )
                            }
                        }
                    }
                }


                HeartTouchView(
                    letter = selectedLetter,
                    descriptions = heartData[selectedLetter] ?: emptyList()
                )

                Spacer(Modifier.height(24.dp))

                HeartInfoTable(
                    heartData = heartData,
                    selected = selectedLetter,
                    onSelect = { letter ->
                        selectedLetter = letter
                    }
                    , navController = navController,  boardList = boardList // 추가
                )



            }
        }
    )





}

@Composable
fun GroupDropdown(
    groupList: List<ApiResponseModel.MemberInfo>,
    onSelected: (ApiResponseModel.MemberInfo) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedGroup by remember { mutableStateOf<ApiResponseModel.MemberInfo?>(null) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.TopEnd) // ✅ 오른쪽에 메뉴 기준 잡기
    ) {
        OutlinedButton(onClick = { expanded = true }) {
            Text(text = selectedGroup?.let { "${it.mem_id}(${it.mem_name})" } ?: "학생선택")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(180.dp) // 옵션: 메뉴 너비 고정
        ) {
            groupList.forEach { group ->
                DropdownMenuItem(onClick = {
                    selectedGroup = group
                    expanded = false
                    onSelected(group)
                }) {
                    Text("${group.mem_id}(${group.mem_name})")
                }
            }
        }
    }
}

@Composable
fun HeartInfoTable(
    heartData: Map<String, List<String>>,
    selected: String?,
    onSelect: (String) -> Unit,
    navController: NavController?,
    boardList: List<ApiResponseModel.Board> = emptyList(), // ← 추가
    modifier: Modifier = Modifier
) {
    val lettersInOrder = listOf("H", "E", "A", "R", "T")
    val perGroupCount = heartData.values.firstOrNull()?.size ?: 4

    // 🔥 board_category_idx Set 만들기
    val activeCodes = remember(boardList) {
        boardList.mapNotNull { it.board_category_idx }.toSet()
    }

    Column(modifier = modifier.fillMaxWidth()) {
        if (selected != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xffebebeb))
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = Color(0xFFFFC0CB),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "차근차근 도전",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(5.dp))

        heartData.forEach { (letter, items) ->
            if (selected == null || selected == letter) {
                val letterIndex = lettersInOrder.indexOf(letter)

                // 🔥 해당 글자의 코드 리스트 (예: H → TOU001~004)
                val codesForLetter = (0 until perGroupCount).map { idx ->
                    "TOU" + String.format("%03d", letterIndex * perGroupCount + idx + 1)
                }

                // 🔥 모두 활성화된 경우만 분홍색
                val isAllActive = codesForLetter.all { it in activeCodes }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),  verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clickable { onSelect(letter) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = if (isAllActive) Color(0xFFFFC0CB) else Color(0xFFEBEBEB), // ✅ 여기 수정
                            modifier = Modifier.fillMaxSize()
                        )
                        Text(
                            text = letter,
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }


                    items.forEachIndexed { idx, text ->
                        val letterIndex = lettersInOrder.indexOf(letter)
                        val globalIndex = letterIndex * perGroupCount + idx
                        val code = "TOU" + String.format("%03d", globalIndex + 1)

                        // 🔥 색상 결정 로직
                        val bg = when {
                            code in activeCodes -> {
                                if (idx < 2) Color(0xFFFFF59B) else Color(0xFFA1EEBD)
                            }
                            else -> Color(0xFFE0E0E0) // 회색
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp)
                                .background(bg, RoundedCornerShape(8.dp))
                                .clickable {
                                    navController?.navigate("touchDetail/$code/$text")
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = text,
                                color = Color.Black,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HeartTouchView(
    letter: String?,
    descriptions: List<String>,
    modifier: Modifier = Modifier
) {
    // 선택된 글자가 없으면 기본 안내 UI
    if (letter == null) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = Color.LightGray,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(16.dp)) // 하트와 텍스트 간격
                // 분홍 하트
                Image(
                    painter = painterResource(id = R.drawable.touch),
                    contentDescription = "Touch Icon",
                    modifier = Modifier.size(40.dp)
                )


                // 2. 텍스트 컬럼
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),

                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "마음터치",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier
                                .background(
                                    color = Color(0xFFFF6F91), // 진한 분홍색
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp)) // 마음터치와 문장 사이 간격
                        Text(
                            text = "는 수업시간에 선생님과",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp)) // 위-아래 줄 간격
                    Text(
                        text = "함께 참여하는 공간이에요.",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black
                    )
                }
            }
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(Color(0xFFFFEBEE), RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            // 1) 분홍 하트 + 선택된 글자 & 우측 설명
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // 분홍 하트 + 글자
                Box(
                    modifier = Modifier.size(72.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = Color(0xFFFFC0CB),
                        modifier = Modifier.fillMaxSize()
                    )
                    Text(
                        text = letter,
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

//                if (letter=="H"){
//                    var text = "이번 활동에서는 마음을 알아보고, 내 기분을 알아볼 수 있어 좋은 친구들 관계를 만들어 갈 수 있어요",
//                }
                // 우측에 추가된 설명 텍스트
                Text(
                    text = "이번 활동에서는 마음을 알아보고, 내 기분을 알아볼 수 있어 좋은 친구들 관계를 만들어 갈 수 있어요",
                    fontSize = 13.sp,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
