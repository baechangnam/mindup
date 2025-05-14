package apps.kr.mentalgrowth.ui.main

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import apps.kr.mentalgrowth.R
import apps.kr.mentalgrowth.common.CommonView
import apps.kr.mentalgrowth.common.CommonView.TitleWithHearts
import apps.kr.mentalgrowth.model.ApiResponseModel
import apps.kr.mentalgrowth.navigation.BottomNavigationBar
import apps.kr.mentalgrowth.ui.main.viewmodel.MainViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NoticeScreen(navController: NavController, viewModel: MainViewModel = viewModel()) {
    val boardList by viewModel.getBoardList.collectAsState()

    val class_group_id = LocalContext.current
        .getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        .getString("class_group_id", "")!!

    LaunchedEffect(class_group_id) {
        viewModel.getBoard("1", class_group_id,"")
    }
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    var selectedBoard by remember { mutableStateOf<ApiResponseModel.Board?>(null) }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            selectedBoard?.let {
                NoticeDetailSheet(board = it) {
                    scope.launch { sheetState.hide() }
                }
            }
        }
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
                        TitleWithHearts("공지사항")

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
        ) { innerPadding ->

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()

                ) {
                    // 상단 공지 영역


                    // Board 리스트 영역
                    if (boardList.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "검색결과가 없습니다", color = Color.Gray,  style = MaterialTheme.typography.body2)
                        }
                    } else {
//                        LazyColumn(modifier = Modifier.fillMaxSize()) {
//                            items(boardList) { board ->
//                                BoardItemNotice(board = board) {
//                                    selectedBoard = board
//                                    scope.launch { sheetState.show() }
//                                }
//                                Divider(modifier = Modifier.fillMaxWidth(), color = Color.LightGray)
//                            }
//                        }
                    }
                }
            }
        }
    }
}


@Composable
fun BoardItemNotice(board: ApiResponseModel.Board, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Spacer(modifier = Modifier.height(10.dp))
//            CommonView.CaddyTextBold(
//                text = board.title,
//                fontSizeDp = 15,
//                color = Color.Black,
//                maxLines = 1
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            CommonView.CaddyTextLight(
//                text = "${board.reg_id_name} | ${board.reg_date.substring(0, 10)} | 조회: ${board.hit}",
//                fontSizeDp = 12,
//                color = Color.Black,
//                maxLines = 1
//            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun NoticeDetailSheet(board: ApiResponseModel.Board, onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = board.title,
            style = MaterialTheme.typography.h6,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "${board.reg_id_name} | ${board.reg_date.substring(0, 10)} | 조회: ${board.hit}",
            style = MaterialTheme.typography.caption,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = board.contents,
            style = MaterialTheme.typography.body1,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onClose, modifier = Modifier.align(Alignment.End)) {
            Text("닫기",  style = MaterialTheme.typography.body2)
        }
    }
}