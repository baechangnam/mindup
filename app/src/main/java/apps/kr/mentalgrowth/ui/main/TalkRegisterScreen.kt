

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.FavoriteBorder
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import apps.kr.mentalgrowth.R
import apps.kr.mentalgrowth.common.CommonView.TitleWithHearts
import apps.kr.mentalgrowth.network.NetworkClient.apiService
import apps.kr.mentalgrowth.ui.login.createPartFromString
import apps.kr.mentalgrowth.ui.login.getDisplayName
import apps.kr.mentalgrowth.ui.login.prepareFilePartAllFile
import apps.kr.mentalgrowth.ui.main.viewmodel.MainViewModel

import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.buffer
import okio.sink
import okio.source
import java.io.File

@Composable
fun TalkRegisterScreen(navController: NavController, title : String,  idx: String? = null, viewModel: MainViewModel = viewModel()) {
    // 제목, 내용 입력 상태
    val context = LocalContext.current

    val prefs = context.getSharedPreferences("board_draft", Context.MODE_PRIVATE)
    var title by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    val class_group_id = LocalContext.current
        .getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        .getString("class_group_id", "")!!

    val mem_id = LocalContext.current
        .getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        .getString("mem_id", "")!!

    LaunchedEffect(idx) {
        if (!idx.isNullOrBlank()) {
            viewModel.fetchBoardDetailTouch(idx, mem_id)
        }
    }
    val board by viewModel.board.collectAsState()
    LaunchedEffect(board) {
        board?.let {
            title = it.title
        }
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
                    TitleWithHearts("마음쓰기 등록")

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
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(scrollState), // 스크롤 추가
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(Modifier.height(5.dp))

                Column(
                    horizontalAlignment = Alignment.Start
                ) {


                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFFFB6C1), RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("마음쓰기", color = Color.White, fontSize = 14.sp,style = MaterialTheme.typography.body2)
                        }

                        Spacer(modifier = Modifier.width(5.dp))

                        Text(
                            "오늘의 내 마음과 관련해 나에게 편지를 써 보아요.",
                            fontSize = 13.sp,
                            color = Color(0xFFDA2C43)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.mail), // ✉️ 편지 이미지 리소스
                        contentDescription = "편지 아이콘",
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "마음편지를 써봅시다",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF333333)
                    )
                }
                // 제목 입력란
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    // 배경 이미지
                    Image(
                        painter = painterResource(id = R.drawable.box),
                        contentDescription = "편지지 배경",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier.matchParentSize()
                    )

                    // 실제 텍스트 필드
                    BasicTextField(
                        value = title,
                        onValueChange = { title = it },
                        textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                        modifier = Modifier
                            .fillMaxSize(),
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 45.dp, vertical = 30.dp) // ✅ 내부 패딩 적용
                            ) {
                                if (title.isEmpty()) {
                                    Text(
                                        text = "",
                                        fontSize = 16.sp,
                                        color = Color.Gray
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                val scope = rememberCoroutineScope()
                // 등록 버튼
                Button(
                    onClick = {
                        scope.launch {
                            try {
                                val response = apiService.regTalk(
                                    "5",
                                    title,
                                    idx.toString(),
                                    mem_id,
                                    class_group_id
                                )
                                if (response.isSuccessful) {
                                    response.body()?.let { list ->
                                        if (list.flag.flag == "1") {
                                            Toast.makeText(context, "등록 완료", Toast.LENGTH_SHORT).show()
                                            navController?.popBackStack()
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("myLog", "등록 오류: $e")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp) // ✅ 버튼 높이 키움
                        .padding(top = 16.dp) // 위 여백
                ) {
                    Text(
                        text = "등록"
                        ,  style = MaterialTheme.typography.body2,
                        fontWeight = FontWeight.Bold
                    )
                }








            }
        }
    )
}
