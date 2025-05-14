package apps.kr.mentalgrowth.ui.main


import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import apps.kr.mentalgrowth.R
import apps.kr.mentalgrowth.common.CommonView.TitleWithHearts
import apps.kr.mentalgrowth.model.ApiResponseModel
import apps.kr.mentalgrowth.ui.main.viewmodel.MainViewModel



@Composable
fun NoticeListScreen(navController: NavController, viewModel: MainViewModel = viewModel()) {
    val boardList by viewModel.getNoticeList.collectAsState()

    val topTitle by viewModel.getBoardTitle.collectAsState()
    val topIdx by viewModel.getBoardIdx.collectAsState()

    val context = LocalContext.current

    val class_group_id = LocalContext.current
        .getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        .getString("class_group_id", "")!!

    val mem_id = LocalContext.current
        .getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        .getString("mem_id", "")!!

    var showNoPermissionDialog by remember { mutableStateOf(false) }

    LaunchedEffect(class_group_id) {
        viewModel.getNotice("1", class_group_id,"0")
        viewModel.getNoticeTitle("1", class_group_id,"1")
    }

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


        }
    ) { innerPadding ->
        if (showNoPermissionDialog) {
            AlertDialog(
                onDismissRequest = { showNoPermissionDialog = false },
                title = { Text(text = "권한 없음",style = MaterialTheme.typography.body2) },
                text = { Text(text = "글쓰기 권한이 없습니다. 메뉴에서 게시판 권한을 신청해주세요.",style = MaterialTheme.typography.body2) },
                confirmButton = {
                    TextButton(onClick = { showNoPermissionDialog = false }) {
                        Text("확인")
                    }
                }
            )
        }
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {

                            navController.navigate("board_detail/notice/${topIdx}")
                             } // 👉 클릭 이벤트 추가
                        .background(Color(0xFFFFC0CB), shape = RoundedCornerShape(8.dp)) // 분홍색 배경
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "공지",
                        color = Color.Red,
                        fontSize = 15.sp,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.width(8.dp))
                    // 한 줄로 표시되는 공지 내용 (maxLines=1)

                    Text(
                        text = topTitle,
                        color = Color.Black,
                        fontSize = 14.sp,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Board 리스트 영역
                if (boardList.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "검색결과가 없습니다", color = Color.Gray,  style = MaterialTheme.typography.body2)
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(boardList) { board ->
                            BoardItem(board = board) {
                                navController.navigate("board_detail/notice/${board.idx}")
                            }
                            Divider(modifier = Modifier.fillMaxWidth(), color = Color.LightGray)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun BoardItem(
    board: ApiResponseModel.Board,
    onClick: () -> Unit   // 클릭 시 호출할 콜백 추가
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() }, // 클릭 이벤트 처리
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 좌측: 제목, 등록자, 날짜, 조회 수를 세로로 배치
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = board.title,
                color = Color.Black,
                fontSize = 14.sp,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${
                    board.reg_date.substring(
                        0,
                        10
                    )
                } | 조회: ${board.hit}",
                color = Color.Black,
                fontSize = 12.sp,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(5.dp))
        }
        // 우측: 댓글 개수를 표시하는 영역 (둥근 배경)

    }
}