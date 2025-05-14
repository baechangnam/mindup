package apps.kr.mentalgrowth.ui.main

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
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
import apps.kr.mentalgrowth.network.NetworkClient.apiService
import apps.kr.mentalgrowth.ui.main.viewmodel.MainViewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import java.time.format.TextStyle

//import apps.kr.mentalgrowth.ui.main.viewmodel.GolfCourseListViewModel

@Composable
fun MindChallengeNoteScreen( navController: NavController? = null,
                     viewModel: MainViewModel = viewModel()) {

    val boardList by viewModel.getBoardList.collectAsState()
    val boardVideoList by viewModel.getBoardVideo.collectAsState()
    val getBoardTitle by viewModel.getBoardTitle.collectAsState()
    val memberList by viewModel.memList.collectAsState()

    val nickName = LocalContext.current
        .getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        .getString("mem_nick", "닉네임")!!

    val class_group_id = LocalContext.current
        .getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        .getString("class_group_id", "")!!

    val mem_id = LocalContext.current
        .getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        .getString("mem_id", "")!!

    // ① 선택된 글자 상태
    var selectedLetter by remember { mutableStateOf<String?>(null) }

    // heartData 재사용을 위해 따로 정의
    val heartData = mapOf(
        "H" to listOf("마음 일기", "마음 컬러링"),
        "E" to listOf("마음 브이로그", "마음 비타민"),
        "A" to listOf("마음 약국", "마음 알람"),
        "R" to listOf("1일 1심", "마음 히어로"),
        "T" to listOf("마음 테라피", "마음 다이어트")
    )

    LaunchedEffect(class_group_id) {
        viewModel.getBoardTouch("4", class_group_id,"",mem_id,"note")

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
                    TitleWithHearts("성장노트")

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
                Spacer(Modifier.height(24.dp))

                Box(
                    modifier = Modifier
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
                        Spacer(modifier = Modifier.width(12.dp)) // 하트와 텍스트 간격
                        // 분홍 하트
                        Image(
                            painter = painterResource(id = R.drawable.challenge),
                            contentDescription = "Touch Icon",
                            modifier = Modifier.size(40.dp)
                        )


                        Column(
                            modifier = Modifier
                                .fillMaxWidth(),

                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "성장노트",
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
                                    text = "는 스스로 성장한 결과를",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color.Black
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp)) // 위-아래 줄 간격
                            Text(
                                text = "한 문장으로 적어보는 공간이에요",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.Black
                            )

                        }
                    }

                }



                Spacer(Modifier.height(24.dp))

                HeartInfoTableD(
                    heartData = heartData,
                    selected = selectedLetter,
                    onSelect = { letter ->
                        selectedLetter = letter
                    }
                    , navController = navController,  boardList = boardList // 추가
                )

                Spacer(modifier = Modifier.height(16.dp))





            }
        }
    )




}
@Composable
fun HeartInfoTableD(
    heartData: Map<String, List<String>>,
    selected: String?,
    onSelect: (String) -> Unit,
    navController: NavController?,
    boardList: List<ApiResponseModel.Board> = emptyList(),
    modifier: Modifier = Modifier
) {
    val lettersInOrder = listOf("H", "E", "A", "R", "T")
    val perGroupCount = heartData.values.firstOrNull()?.size ?: 2
    val mem_id = LocalContext.current
        .getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        .getString("mem_id", "")!!
    val activeCodes = remember(boardList) {
        boardList.mapNotNull { it.board_category_idx }.toSet()
    }

    // ✅ 각 글자마다 상태 따로 관리 (입력값)
    val inputMap = remember { mutableStateMapOf<String, String>() }
    var shortMemo by remember { mutableStateOf("H") }
    val class_group_id = LocalContext.current
        .getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        .getString("class_group_id", "")!!

    val idxMap = remember { mutableStateMapOf<String, Int>() }

    // ✅ 여기에 선언합니다!
    LaunchedEffect(boardList) {
        listOf("H", "E", "A", "R", "T").forEach { letter ->
            val matched = boardList.find { it.short_memo == letter }
            if (matched != null) {
                inputMap[letter] = matched.title ?: ""
                idxMap[letter] = matched.idx
            }
        }
    }
    val context = LocalContext.current

    var showDeleteDialogFor by remember { mutableStateOf<String?>(null) } // 현재 삭제 대상 letter
    val scope = rememberCoroutineScope()

    Column(modifier = modifier.fillMaxWidth()) {

        Spacer(modifier = Modifier.height(5.dp))

        heartData.forEach { (letter, items) ->
            if (selected == null || selected == letter) {
                val letterIndex = lettersInOrder.indexOf(letter)
                val codesForLetter = (0 until perGroupCount).map { idx ->
                    "CAL" + String.format("%03d", letterIndex * perGroupCount + idx + 1)
                }
                val context = LocalContext.current
                val isAllActive = codesForLetter.all { it in activeCodes }
                val hasInput = inputMap[letter]?.isNotBlank() == true

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 2.dp)
                ) {
                    // ✅ 상단 아이콘 + 문자 (클릭 시 선택)
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp).clickable {
                                   // onSelect(letter)
                                    shortMemo = letter
                                },

                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = if (hasInput) Color(0xFFFFC0CB) else Color(0xFFEBEBEB),
                                modifier = Modifier.fillMaxSize()
                            )
                            Text(
                                text = letter,
                                color = Color.Black,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))
                        var textFieldFocusState by remember { mutableStateOf(false) }
                        // ✅ 입력창 + 등록버튼
                        TextField(
                            value = inputMap[letter] ?: "",
                            onValueChange = { inputMap[letter] = it },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp) // 최소 높이 설정
                                .padding(vertical = 4.dp).pointerInput(Unit) {
                                    detectTapGestures(
                                        onLongPress = {
                                            if (!inputMap[letter].isNullOrBlank()) {
                                                showDeleteDialogFor = letter
                                            }
                                        }
                                    )
                                }.onFocusChanged { focusState ->
                                    textFieldFocusState = focusState.isFocused
                                    if (focusState.isFocused) {
                                        shortMemo = letter
                                        // idxMap[letter]는 그대로 두되, 포커스된 글자를 기준으로 서버로 보내는 shortMemo는 업데이트
                                    }
                                }, // 위아래 여백 축소
                            placeholder = { Text("내용 입력", fontSize = 12.sp) },
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontSize = 13.sp,
                                lineHeight = 16.sp
                            ),
                            singleLine = false,
                            maxLines = 4
                        )

                        Spacer(modifier = Modifier.width(8.dp))
                        val scope = rememberCoroutineScope()
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            horizontalAlignment = Alignment.End,
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Button(
                                onClick = {
                                    val input = inputMap[letter]
                                    val idx = idxMap[letter]
                                    scope.launch {
                                        try {
                                            val response = apiService.regNote(
                                                input ?: "",
                                                idx.toString(),
                                                mem_id,
                                                shortMemo,
                                                class_group_id
                                            )
                                            if (response.isSuccessful) {
                                                response.body()?.let { list ->
                                                    if (list.flag.flag == "1") {
                                                        Toast.makeText(context, "등록 완료", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            }
                                        } catch (e: Exception) {
                                            Log.e("myLog", "등록 오류: $e")
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .width(50.dp)
                                    .height(24.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("등록", fontSize = 9.sp)
                            }

                            Button(
                                onClick = {
                                    if (!inputMap[letter].isNullOrBlank()) {
                                        showDeleteDialogFor = letter
                                    }
                                },
                                modifier = Modifier
                                    .width(50.dp)
                                    .height(24.dp),
                                contentPadding = PaddingValues(0.dp),
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray)
                            ) {
                                Text("삭제", fontSize = 9.sp)
                            }
                        }
                    }
                }
            }

        }

        // ✅ 반드시 Column 내부에 위치해야 함!
        if (showDeleteDialogFor != null) {
            val targetLetter = showDeleteDialogFor!!
            val idx = idxMap[targetLetter]

            AlertDialog(
                onDismissRequest = { showDeleteDialogFor = null },
                title = { Text("삭제 확인") },
                text = { Text("정말 삭제하시겠습니까?") },
                confirmButton = {
                    TextButton(onClick = {
                        scope.launch {
                            try {
                                if (idx != null) {
                                    val response = apiService.deleteBoard(idx.toString())
                                    if (response.isSuccessful) {
                                        inputMap[targetLetter] = ""
                                        idxMap.remove(targetLetter)
                                        Toast.makeText(context, "삭제 완료", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("myLog", "삭제 오류: $e")
                            } finally {
                                showDeleteDialogFor = null
                            }
                        }
                    }) {
                        Text("확인")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialogFor = null }) {
                        Text("취소")
                    }
                }
            )
        }


    }

}


