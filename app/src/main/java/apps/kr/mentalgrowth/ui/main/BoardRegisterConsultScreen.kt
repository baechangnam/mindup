

import android.content.Context
import android.net.Uri
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
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
import apps.kr.mentalgrowth.common.CommonView
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
fun BoardRegisterConsultScreen(navController: NavController, code : String,  idx: String? = null, viewModel: MainViewModel = viewModel()) {
    // 제목, 내용 입력 상태
    val context = LocalContext.current
    // 글자 속성 상태
    var selectedFontSize by remember { mutableStateOf("보통") } // 옵션: "큰", "보통", "작은"
    var isBold by remember { mutableStateOf(false) }
    var isItalic by remember { mutableStateOf(false) }
    var isUnderline by remember { mutableStateOf(false) }
    var isStrikethrough by remember { mutableStateOf(false) }
    // 12가지 주요 색상 예시

    var selectedColor by remember { mutableStateOf(Color.Black) }

    // 사진 첨부 (예시 상태; 실제로는 이미지 Uri나 Bitmap 등을 사용)
    var attachedPhoto by remember { mutableStateOf<Any?>(null) }

    // 선택된 글자 크기를 sp로 변환
    val fontSizeSp = when (selectedFontSize) {
        "큰" -> 20.sp
        "보통" -> 16.sp
        "작은" -> 12.sp
        else -> 16.sp
    }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var consentName by remember { mutableStateOf<String?>(null) }
    // 이미지 선택 런처 등록: image/* 타입만 받음
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = null
        consentName = null

        if (uri != null) {
            val sizeInBytes = context.contentResolver.query(
                uri,
                null, null, null, null
            )?.use { cursor ->
                val sizeIndex = cursor.getColumnIndex(android.provider.OpenableColumns.SIZE)
                if (cursor.moveToFirst() && sizeIndex >= 0) {
                    cursor.getLong(sizeIndex)
                } else null
            }

            if (sizeInBytes != null && sizeInBytes <= 15 * 1024 * 1024) { // 15MB 이하
                imageUri = uri
                consentName = uri.getDisplayName(context)
            } else {
                Toast.makeText(context, "15MB 이하 파일만 첨부할 수 있습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    val prefs = context.getSharedPreferences("board_draft", Context.MODE_PRIVATE)
    var title by remember { mutableStateOf(prefs.getString("draft_title", "") ?: "") }
    var content by remember { mutableStateOf(prefs.getString("draft_content", "") ?: "") }
    var addr by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(0f) }
    val scrollState = rememberScrollState()

    val memId = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        .getString("mem_id", "") ?: ""

    LaunchedEffect(idx) {
        if (!idx.isNullOrBlank()) {
            viewModel.fetchBoardDetailTouch(idx, memId)
        }
    }
    val board by viewModel.board.collectAsState()
    LaunchedEffect(board) {
        board?.let {
            title = it.title
            content = it.contents
            addr = it.addr?:""
            rating = it.point?.toFloat() ?: 0f
            consentName = it.filename

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
                    CommonView.TitleWithHearts("게시판 등록")

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
                // 제목 입력란
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("제목",  style = MaterialTheme.typography.body2) },
                    modifier = Modifier.fillMaxWidth()
                )


                // 내용 입력란 (실시간으로 스타일이 적용됨)
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("내용",  style = MaterialTheme.typography.body2) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    textStyle = TextStyle(
                        fontSize = fontSizeSp,
                        fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                        fontStyle = if (isItalic) FontStyle.Italic else FontStyle.Normal,
                        textDecoration = when {
                            isUnderline && isStrikethrough -> TextDecoration.combine(
                                listOf(TextDecoration.Underline, TextDecoration.LineThrough)
                            )
                            isUnderline -> TextDecoration.Underline
                            isStrikethrough -> TextDecoration.LineThrough
                            else -> TextDecoration.None
                        },
                        color = selectedColor
                    )
                )


//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    modifier = Modifier.padding(bottom = 8.dp)
//                ) {
//                    Text("자기평가: ", modifier = Modifier.padding(end = 8.dp))
//
//                    HeartRatingBar(rating = rating, onRatingChanged = { rating = it })
//                }
                OutlinedTextField(
                    value = addr,
                    onValueChange = { addr = it },
                    label = { Text("링크",  style = MaterialTheme.typography.body2) },
                    modifier = Modifier.fillMaxWidth()
                )



                Button(onClick = { launcher.launch("*/*") }) {
                    Text(text = "파일 첨부",  style = MaterialTheme.typography.body2)

                }
                consentName?.let {
                    Text(it, fontSize = 14.sp)
                }


//                // 선택된 이미지 미리보기
//                imageUri?.let { uri ->
//                    Image(
//                        painter = rememberAsyncImagePainter(model = uri),
//                        contentDescription = "파일 이미지",
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(100.dp)
//                    )
//                }




                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // 실제 등록 버튼
                    UploadBoardButtonC(
                        pid = "12",
                        title = title,
                        content = content,
                        imageUri = imageUri,
                        code = code,
                        addr = addr,
                        rating = rating,
                        idx = idx?:"",
                        onUploadSuccess = {
                            // 업로드 성공 후 처리 (예: 이전 화면으로 돌아가기)
                            prefs.edit().apply {
                                putString("draft_title", "")
                                putString("draft_content", "")
                                apply()
                            }

                            Toast.makeText(context, "등록 완료되었습니다.", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }, onTitleEmpty = {
                            Toast.makeText(context, "제목을 입력하세요.", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

            }
        }
    )
}
