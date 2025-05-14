package apps.kr.mentalgrowth.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.system.Os.close
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
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
import apps.kr.mentalgrowth.ui.login.TeacherSignUpButton
import apps.kr.mentalgrowth.ui.login.createPartFromString
import apps.kr.mentalgrowth.ui.login.prepareFilePart
import apps.kr.mentalgrowth.ui.login.prepareFilePartAllFile
import apps.kr.mentalgrowth.ui.main.viewmodel.MainViewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProfileEditScreen(
    navController: NavController? = null,
    viewModel: MainViewModel = viewModel(),   onLogout: () -> Unit
) {
    val context = LocalContext.current
    val class_group_id = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        .getString("class_group_id", "")!!
    val mem_id = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        .getString("mem_id", "")!!

    val memberList by viewModel.memList.collectAsState()
    val member = memberList.firstOrNull()

    // 닉네임 상태
    var nickName by remember { mutableStateOf(member?.mem_phone ?: "") }


    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(class_group_id) {
        viewModel.getMyInfo(mem_id)
    }

    LaunchedEffect(member) {
        member?.mem_phone?.let {
            nickName = it
        }
    }

    val prefs = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)


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
                    TitleWithHearts("프로필 수정")

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
        content = {
            if (member == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("회원 정보를 불러오는 중입니다...", color = Color.Gray)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val imageUrl = member.mem_img?.takeIf { it.isNotBlank() }?.let {
                        NetworkClient.BASE_URL_MEMBER + it
                    }

                    AsyncImage(
                        model = selectedImageUri ?: (imageUrl ?: R.drawable.thumbnail_no_img1),
                        contentDescription = "프로필 이미지",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                    )

                    Spacer(modifier = Modifier.height(8.dp))


                    Text(
                        text = "${member.mem_name ?: "0"}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    Text(
                        text = "${member.school.substringBefore("초")}초 ${member.mem_hack ?: "0"}학년 ${member.mem_ban ?: "0"}반",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "나의 글: ${member.board_cnt ?: "0"}개   나의 댓글: ${member.comment_cnt ?: "0"}개",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 🔽 프로필 이미지 수정 버튼
                    OutlinedButton(
                        onClick = {
                            imagePickerLauncher.launch("image/*")
                        },
                        shape = RoundedCornerShape(50)
                    ) {
                        Text("프로필 이미지 수정", style = MaterialTheme.typography.body2)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 🔽 닉네임 입력 필드
                    Text(
                        "닉네임 수정",
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.Start) // ← 왼쪽 정렬
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    TextField(
                        value = nickName,
                        onValueChange = { nickName = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(32.dp))



                    UploadButton(
                        userId          = mem_id,
                        nickName        = nickName,
                        selectedImageUri  = selectedImageUri,

                        onSignUpSuccess = {
                            navController?.popBackStack()
                        }
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = { showLogoutDialog = true },
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        Text("로그아웃", color = Color.Black, fontSize = 16.sp)
                    }
                }
            }

            if (showLogoutDialog) {
                AlertDialog(
                    onDismissRequest = { showLogoutDialog = false },
                    title = {
                        Text(text = "로그아웃"  ,style = MaterialTheme.typography.body2)
                    },
                    text = {
                        Text("정말 로그아웃 하시겠습니까?" ,style = MaterialTheme.typography.body2)
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            showLogoutDialog = false
                            onLogout()
                        }) {
                            Text("확인" ,style = MaterialTheme.typography.body2)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showLogoutDialog = false
                        }) {
                            Text("취소" ,style = MaterialTheme.typography.body2)
                        }
                    }
                )
            }
        }
    )
}


@Composable
fun UploadButton(
    userId: String,
    nickName: String,
    selectedImageUri: Uri?,

    // 가입 성공 시 처리
    onSignUpSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val memId = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        .getString("mem_id", "") ?: ""
    val apiService = NetworkClient.apiService

    Button(
        onClick = {
            // 1) 빠진 필드 체크
            val userIdPart      = createPartFromString(userId)
            val nickNamePart    = createPartFromString(nickName)
            val memIdPart       = createPartFromString(memId)

            val imagePart =
                selectedImageUri?.let { prepareFilePartAllFile(context, it, "image") }


            val isUpdatePart     = createPartFromString(
                if (selectedImageUri != null) "1" else "0"
            )


            // 3) 백그라운드에서 업로드
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = apiService.updateMember(
                        userId      = userIdPart,
                        nickName    = nickNamePart,
                        memId       = memIdPart,
                        isUpdate = isUpdatePart,
                        image = imagePart
                    )
                    if (response.isSuccessful && response.body()?.flag?.flag == "1") {
                        // 메인 스레드에서 후속 처리
                        launch(Dispatchers.Main) {
                            Toast.makeText(context, "수정이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                            onSignUpSuccess()
                        }
                    } else {
                        launch(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                response.body()?.flag?.message ?: "회원가입 실패",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    launch(Dispatchers.Main) {
                        Toast.makeText(context, "예외 발생: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(50)
    ) {
        Text("저장하기", color = Color.White,  style = MaterialTheme.typography.body1)
    }
}

