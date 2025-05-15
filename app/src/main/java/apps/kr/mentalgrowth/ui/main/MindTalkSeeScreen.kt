package apps.kr.mentalgrowth.ui.main

import android.content.Context
import android.system.Os.close
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.rememberBottomSheetScaffoldState
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
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
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MindTalkSeeScreen( navController: NavController? = null,
                       viewModel: MainViewModel = viewModel()
) {
    val boardList by viewModel.getBoardList.collectAsState()
    val memberList by viewModel.memList.collectAsState()
    var selTitle by remember { mutableStateOf("") }

    val context = LocalContext.current
    val class_group_id = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        .getString("class_group_id", "")!!
    val mem_id = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        .getString("mem_id", "")!!


    var selectedBoard by remember { mutableStateOf<ApiResponseModel.Board?>(null) }
    val scaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()
    var showMenu by remember { mutableStateOf(false) }
    val isRefreshing = remember { mutableStateOf(false) }

    val groupList by viewModel.groupList.collectAsState()
    val mem_level = LocalContext.current
        .getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        .getString("mem_level", "")!!




    LaunchedEffect(class_group_id) {
        viewModel.getBoardTalk("5", class_group_id, "", "", "")
        viewModel.getMemInfo(mem_id,class_group_id)
    }

    LaunchedEffect(selectedBoard) {
        if (selectedBoard != null) {
            scaffoldState.bottomSheetState.expand()
        }
    }

    BackHandler(enabled = scaffoldState.bottomSheetState.isExpanded) {
        scope.launch {
            scaffoldState.bottomSheetState.collapse()
        }
    }
    fun formatRegDate(raw: String): String {
        return try {
            val parser = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
            val date = parser.parse(raw)
            val formatter = java.text.SimpleDateFormat("M.d", java.util.Locale.getDefault())
            formatter.format(date!!)
        } catch (e: Exception) {
            ""
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetContent = {
            selectedBoard?.let { board ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),

                        ) {
                        Box(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // 오른쪽 상단 고정
                            Row(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                            ) {
                                var showMenu by remember { mutableStateOf(false) }

                                IconButton(onClick = { showMenu = true }) {
                                    Icon(Icons.Default.MoreVert, contentDescription = "더보기")
                                }

                                DropdownMenu(
                                    expanded = showMenu,
                                    onDismissRequest = { showMenu = false }
                                ) {
                                    if (board?.reg_id == mem_id) {
                                        DropdownMenuItem(onClick = {
                                            showMenu = false
                                            navController?.navigate("board_reg_box/${board.title}?idx=${board.idx}")
                                        }) {
                                            Text("수정")
                                        }
                                        DropdownMenuItem(onClick = {
                                            showMenu = false
                                            scope.launch {
                                                try {
                                                    val response = NetworkClient.apiService.deleteBoard(board.idx.toString()
                                                    )
                                                    if (response.isSuccessful) {
                                                        response.body()?.let { list ->
                                                            if (list.flag.flag == "1") {
                                                                Toast.makeText(context, "삭제 완료", Toast.LENGTH_SHORT).show()
                                                                viewModel.getBoardTalk("5", class_group_id, "", "", "")
                                                            }
                                                        }
                                                    }
                                                } catch (e: Exception) {
                                                    Log.e("myLog", "등록 오류: $e")
                                                }
                                            }
                                            scope.launch { scaffoldState.bottomSheetState.collapse() }
                                        }) {
                                            Text("삭제")
                                        }
                                    }

                                    DropdownMenuItem(onClick = {
                                        showMenu = false
                                        Toast.makeText(context, "신고 접수되었습니다", Toast.LENGTH_SHORT).show()
                                    }) {
                                        Text("신고")
                                    }
                                }
                            }
                        }


                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .clip(RoundedCornerShape(80.dp)) // ✅ 크게 둥근 테두리
                                .background(Color(0xFFFDFDFD))   // ✅ 연한 배경 (선택사항)
                                .border(1.dp, Color.LightGray, RoundedCornerShape(80.dp)) // ✅ 테두리 추가
                        ) {
                            Text(
                                text = board.title,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.DarkGray,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(12.dp),
                                textAlign = TextAlign.Center
                            )
                        }

                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(board.contents ?: "", fontSize = 14.sp)
                }
            }
        },
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
                    TitleWithHearts("마음보기")

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
                    viewModel.getBoardTalk("5", class_group_id, "", "", "")
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
//
//                    if (mem_level =="LEV003"){
//                        GroupDropdown(
//                            groupList = groupList,
//                            onSelected = {
//                                // 선택된 GroupInfo 처리
//                                Log.d("SelectedGroup", it.toString())
//                                var selId = it.mem_id
//                                viewModel.getBoardTalk("5", class_group_id, "", selId, "")
//
//                            }
//                        )
//                    }

                    Row(
                        modifier = Modifier
                            // 1dp 두께의 회색 테두리, 모서리 반경 8.dp
                            .border(0.5.dp, Color.Gray, RoundedCornerShape(12.dp))
                            // 내부 여백
                            .padding(8.dp)
                            // 부모 Column 내에서 가로 중앙에 배치
                            .align(Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically,
                        // Row 내부 아이템들 가로 방향 중앙 정렬
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF42A5F5), RoundedCornerShape(12.dp)) // 밝은 파랑
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text("마음보기", color = Color.White, fontSize = 14.sp)
                        }

                        Spacer(modifier = Modifier.width(5.dp))

                        Text(
                            "친구의 마음을 차분하게 살펴보며, 서로의 마음을 나눠 보세요.",
                            fontSize = 13.sp,
                            color = Color(0xFF1565C0) // 짙은 파랑
                        )
                    }

                    Spacer(modifier = Modifier.height(15.dp))

                    val items = listOf(null) + boardList

                    items.filterNotNull().chunked(2).forEach { rowItems ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            rowItems.forEach { item ->
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f),
                                    verticalArrangement = Arrangement.Top
                                ) {
                                    // ✅ 닉네임 박스
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                Color(0xffffe3ea),
                                                RoundedCornerShape(16.dp)
                                            ) // ✅ 모든 모서리 라운딩
                                            .padding(
                                                horizontal = 22.dp,
                                                vertical = 4.dp
                                            )        // ✅ 텍스트 감쌈 크기
                                            .align(Alignment.CenterHorizontally)                // ✅ 중앙 정렬
                                    ) {
                                        Text(
                                            text = item.reg_id_name ?: "",
                                            color = Color.DarkGray,
                                            fontSize = 12.sp,
                                            textAlign = TextAlign.Center
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(5.dp))
                                    // ✅ 내용 박스
                                    AirmailBorderBox(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clickable {
                                                scope.launch {
                                                    //   selectedBoard = item
                                                }
                                            }
                                    ) {
                                        Text(
                                            text = item.title,
                                            fontSize = 14.sp,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(12.dp),
                                            color = Color.DarkGray
                                        )
                                    }
                                }
                            }

                            // 짝이 안 맞을 경우 공간 확보
                            if (rowItems.size < 2) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
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

