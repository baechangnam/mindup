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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import apps.kr.mentalgrowth.R
import apps.kr.mentalgrowth.common.CommonView.TitleWithHearts
import apps.kr.mentalgrowth.model.ApiResponseModel
import apps.kr.mentalgrowth.network.NetworkClient
import apps.kr.mentalgrowth.ui.main.viewmodel.MainViewModel
import coil.compose.AsyncImage
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun MindTouchDetailScreen( navController: NavController? = null,
                       viewModel: MainViewModel = viewModel() , code : String , codeName : String
) {

    val boardList by viewModel.getBoardList.collectAsState()


    val nickName = LocalContext.current
        .getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        .getString("mem_nick", "닉네임")!!

    val class_group_id = LocalContext.current
        .getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        .getString("class_group_id", "")!!

    val mem_id = LocalContext.current
        .getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        .getString("mem_id", "")!!

    val commCodeDesc by viewModel.commCodeDesc.collectAsState()

    LaunchedEffect(code) {
        viewModel.fetchCommonCode(code)
        viewModel.getBoardTouch("3",class_group_id,code,"","")
    }

    var selectedLetter by remember { mutableStateOf<String?>(null) }
    val isRefreshing = remember { mutableStateOf(false) }


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
            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing.value),
                onRefresh = {
                    isRefreshing.value = true
                    viewModel.getBoardTouch("3", class_group_id, code, "", "")
                    isRefreshing.value = false
                }
            ) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {
                    Spacer(Modifier.height(24.dp))

                    val activeCodes = listOf(code)

                    Column {
                        HeartHighlightRow(initialCodes = activeCodes, navController = navController)
                        // 그 밑에 실제 컨텐츠…
                    }


                    Spacer(Modifier.height(16.dp))

                    // 전체를 감싸는 보더
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(0.6.dp, Color.Gray, RoundedCornerShape(8.dp))
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // codeName 원형 배경
                        val codeNum = code
                            .removePrefix("TOU")
                            .toIntOrNull()

                        Log.d("myLog", "codeNum " + codeNum)

                        // 노란색으로 처리할 코드 번호 집합
                        val yellowCodes = setOf(1, 2, 5, 6, 9, 10, 13, 14, 17, 18)


                        val bgColor = if (codeNum != null && codeNum in yellowCodes) {
                            Color(0xFFFFF59B)    // 노란색
                        } else {
                            Color(0xFFA1EEBD)    // 녹색
                        }

                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(bgColor)
                                .padding(horizontal = 24.dp, vertical = 12.dp)
                        ) {
                            Text(
                                text = codeName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        // commCodeDesc 텍스트
                        Text(
                            text = commCodeDesc,
                            fontSize = 14.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(onClick = {
                            navController?.navigate("board_reg/$code") // ← idx 생략 OK
                        }) {
                            Text("쓰기", style = MaterialTheme.typography.body2)
                        }
                    }


                    Column(modifier = Modifier.fillMaxSize()) {
                        BoardGrid(
                            boardList = boardList,
                            onItemClick = {
                                navController?.navigate("board_detail_touch/touch/${it.idx}")
                            }
                        )
                    }


                }
            }
            if (isRefreshing.value) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                }
            }




        }


    )



}



@Composable
fun BoardGrid(
    boardList: List<ApiResponseModel.Board>,
    onItemClick: (ApiResponseModel.Board) -> Unit
) {


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // 2×2 그리드: chunked(2)
        boardList.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                rowItems.forEach { board ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)       // 정사각형
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF0F0F0))
                            .clickable { onItemClick(board) }
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        val validImageExtensions = listOf("jpg", "jpeg", "png", "webp", "gif")
                        val filename = board.filename?.lowercase()

                        val isImage = filename
                            ?.takeIf { it.isNotBlank() && it != "null" }
                            ?.let { name ->
                                validImageExtensions.any { ext -> name.endsWith(".$ext") }
                            } == true

                        val imageUrl = if (isImage) {
                            NetworkClient.BASE_URL_UPLOAD + board.filename
                        } else {
                            null
                        }

                        Log.d("myLog", "imageUrl " + imageUrl)

                        if (imageUrl != null) {
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = board.title,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp)),
                                contentScale = ContentScale.Crop,
                                onError = {
                                    Log.e("BoardGrid", "Image load failed: $imageUrl")
                                }
                            )
                        } else {
                            Image(
                                painter = painterResource(R.drawable.thumbnail_no_img),
                                contentDescription = "No Image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = board.title,
                            fontSize = 14.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                // 만약 마지막 row 에 아이템이 1개면 남은 공간 채우기
                if (rowItems.size < 2) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun HeartHighlightRow(
    // H,E,A,R,T 에 매핑된 코드 구간
    codeMap: Map<String, List<String>> = mapOf(
        "H" to (1..4).map { i -> "TOU%03d".format(i) },    // TOU001~004
        "E" to (5..8).map { i -> "TOU%03d".format(i) },    // TOU005~008
        "A" to (9..12).map { i -> "TOU%03d".format(i) },   // TOU009~012
        "R" to (13..16).map { i -> "TOU%03d".format(i) },  // TOU013~016
        "T" to (17..20).map { i -> "TOU%03d".format(i) }   // TOU017~020
    ),
    initialCodes: List<String>,  // 예: listOf("TOU002", "TOU006", ...)
    descriptions: List<String> = listOf("알아봐요", "조절해요", "소통해요", "함께해요", "건강해요"),navController: NavController?
) {
    // 코드→글자 맵 생성
    val codeToLetter = remember(codeMap) {
        codeMap.flatMap { (letter, codes) -> codes.map { code -> code to letter } }
            .toMap()
    }

    // initialCodes 에 대응하는 글자 세트 (중복 제거)
    val selectedLetters = remember(initialCodes) {
        initialCodes.mapNotNull { codeToLetter[it] }.toSet()
    }

    val letters = codeMap.keys.toList()
    1
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        letters.forEachIndexed { idx, letter ->
            val desc = descriptions.getOrNull(idx).orEmpty()
            val isSelected = letter in selectedLetters

            Box(
                modifier = Modifier
                    .weight(1f)             // ➊ 동일 비율 분할
                    .aspectRatio(1f)        // ➋ 정사각형 유지
                    .padding(horizontal = 4.dp)
                    .clickable { navController?.popBackStack()  },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = if (isSelected) Color(0xFFFFC0CB) else Color(0xFFEBEBEB),
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
                        color = if (isSelected) Color.Black else Color.DarkGray,
                        modifier = Modifier.padding(bottom = 1.dp)
                    )
                    Text(
                        text = desc,
                        fontSize = 10.sp,
                        color = if (isSelected) Color.Black else Color.DarkGray,
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
}