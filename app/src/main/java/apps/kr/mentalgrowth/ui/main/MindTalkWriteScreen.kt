package apps.kr.mentalgrowth.ui.main

import android.content.Context
import android.system.Os.close
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Paint

import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
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
fun MindTalkWriteScreen(
    navController: NavController? = null,
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

    val groupList by viewModel.groupList.collectAsState()
    val mem_level = LocalContext.current
        .getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        .getString("mem_level", "")!!

    LaunchedEffect(class_group_id) {
        viewModel.getBoardTalk("5", class_group_id, "", mem_id, "")
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
    val isRefreshing = remember { mutableStateOf(false) }

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
                                            navController?.navigate("board_reg_talk/${board.title}?idx=${board.idx}")
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
                                .height(180.dp) // 원하는 높이
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            // 배경 이미지
                            Image(
                                painter = painterResource(id = R.drawable.box), // 편지지 배경 이미지
                                contentDescription = null,
                                contentScale = ContentScale.FillBounds,
                                modifier = Modifier.matchParentSize()
                            )

                            // 텍스트
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
                    TitleWithHearts("마음쓰기")

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
                    viewModel.getBoardTalk("5", class_group_id, "", mem_id, "")
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

                    if (mem_level =="LEV003"){
                        GroupDropdown(
                            groupList = groupList,
                            onSelected = {
                                // 선택된 GroupInfo 처리
                                Log.d("SelectedGroup", it.toString())
                                var selId = it.mem_id
                                viewModel.getBoardTalk("5", class_group_id, "", selId, "")

                            }
                        )
                    }

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
                                .background(Color(0xFFFFB6C1), RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                "마음쓰기",
                                color = Color.White,
                                fontSize = 14.sp,
                                style = MaterialTheme.typography.body2
                            )
                        }

                        Spacer(modifier = Modifier.width(5.dp))

                        Text(
                            "오늘의 내 마음과 관련해 나에게 편지를 써 보아요.",
                            fontSize = 13.sp,
                            color = Color(0xFFDA2C43)
                        )

                    }

                    val items = listOf(null) + boardList

                    items.chunked(2).forEach { rowItems ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            rowItems.forEach { item ->
                                AirmailBorderBox(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .clickable {
                                            if (item == null) {
                                                navController?.navigate("board_reg_talk/${selTitle}?idx=")
                                            } else {
                                                scope.launch {
                                                    selectedBoard = item // ✅ 이걸 추가해야 시트에 내용이 뜸!
                                                    //scaffoldState.bottomSheetState.expand()
                                                }
                                            }
                                        }
                                ) {
                                    if (item == null) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.plus2),
                                            contentDescription = "등록하기",
                                            tint = Color.Gray,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    } else {
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

@Composable
fun AirmailBorderBox(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
    ) {
        Image(
            painter = painterResource(id = R.drawable.box),
            contentDescription = "편지지 배경",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp), // 내부 여백
            contentAlignment = Alignment.Center,
            content = content
        )
    }
}