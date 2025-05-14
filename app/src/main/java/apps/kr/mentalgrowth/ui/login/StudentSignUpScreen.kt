package apps.kr.mentalgrowth.ui.login


import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import apps.kr.mentalgrowth.R
import apps.kr.mentalgrowth.network.NetworkClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@Composable
fun StudentSignUpScreen(
    navController: NavController, viewModel: LoginViewModel = viewModel(),
    onDownloadTemplate: () -> Unit,
    onDownloadConsent: () -> Unit,
    onUploadBatch: (Uri) -> Unit
) {

    var batchUri by remember { mutableStateOf<Uri?>(null) }
    var batchName by remember { mutableStateOf<String?>(null) }
    var consentUri by remember { mutableStateOf<Uri?>(null) }
    var consentName by remember { mutableStateOf<String?>(null) }
    val boardList by viewModel.getBoardList.collectAsState()

    val filename01 by viewModel._file01.collectAsState()
    val filename02 by viewModel._file02.collectAsState()
    val baseUrl = "https://mindup25.mycafe24.com/upload/pdf"
    var showMissingDialog by remember { mutableStateOf(false) }
    var missingFields by remember { mutableStateOf(listOf<String>()) }

    val pid = "3"
    var keyword by remember { mutableStateOf("") }

    val context = LocalContext.current

    LaunchedEffect(pid) {
        viewModel.fetchBoardList("10", keyword)
    }
// 모든 파일 타입 허용
    val batchLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            batchUri = it
            batchName = it.getDisplayName(context)
            onUploadBatch(it)
        }
    }
    val consentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            consentUri = it
            consentName = it.getDisplayName(context)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 상단: 학생용 + 타이틀
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = colorResource(id = R.color.primary),
                modifier = Modifier.size(30.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "학생용 회원 가입",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()      // 가로 전체를 채우고
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.Start  // 내부 항목들을 왼쪽 정렬
        ) {
            Text("1. 아래 일괄 등록 양식 내려받기", fontSize = 14.sp)
            Spacer(Modifier.height(4.dp))
            Text("2. 보호자 동의서를 받아 PDF로 스캔하여 제출하기", fontSize = 14.sp)
            Spacer(Modifier.height(4.dp))
            Text("3. 일괄 등록 양식 제출하기", fontSize = 14.sp)
            Spacer(Modifier.height(4.dp))
            Text("4. 자료 승인 후 학생 개별 아이디 및 비밀번호 배부하기", fontSize = 14.sp)
            Spacer(Modifier.height(4.dp))
            Text("5. 학생별 아이디 및 비밀번호로 접속 후 최초 비밀번호 변경하기", fontSize = 14.sp)
        }
        Spacer(Modifier.height(32.dp))

        fun enqueueDownload(fileName: String) {
            val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val uri = Uri.parse("$baseUrl/$fileName")
            val request = DownloadManager.Request(uri).apply {
                setTitle(fileName)
                setDescription("두근두근 마음성장 파일 다운로드")
                setMimeType("application/*")
                setNotificationVisibility(
                    DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
                )
                // Android Q 이상에서는 MediaStore API를 이용하거나 아래 주석을 제거하고
                // setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            }
            dm.enqueue(request)
            Toast.makeText(context, "다운로드가 시작되었습니다.", Toast.LENGTH_SHORT).show()
        }

        // 1) 일괄 등록 양식 다운로드 버튼
        Button(
            onClick = { enqueueDownload(filename01) },
            enabled = filename01.isNotBlank(),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFF5E35B1),  // 보라 계열
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("일괄 등록 양식 다운로드",  style = MaterialTheme.typography.body2)
        }

        Spacer(Modifier.height(16.dp))

        // 2) 보호자 동의서 다운로드 버튼
        Button(
            onClick = { enqueueDownload(filename02) },
            enabled = filename02.isNotBlank(),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFF00897B),  // 청록 계열
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("보호자 동의서 다운로드",  style = MaterialTheme.typography.body2)
        }

        Spacer(Modifier.height(16.dp))

        Divider(color = Color.Gray, thickness = 0.7.dp)


        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { batchLauncher.launch(arrayOf("*/*")) },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("일괄 등록 양식 업로드",  style = MaterialTheme.typography.body2)
        }
        batchName?.let {
            Spacer(Modifier.height(4.dp))
            Text(it, fontSize = 14.sp)
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { consentLauncher.launch(arrayOf("*/*")) },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("보호자 동의서 업로드",  style = MaterialTheme.typography.body2)
        }
        consentName?.let {
            Spacer(Modifier.height(4.dp))
            Text(it, fontSize = 14.sp)
        }

        Spacer(Modifier.height(32.dp))

        SignUpButton(
            consentUri  = consentUri,
            batchUri     = batchUri,
            onFieldsEmpty = { missing ->
                missingFields = missing
                showMissingDialog = true
            },
            onSignUpSuccess = {
                navController.navigate("login") {
                    popUpTo("signup/teacher") { inclusive = true }
                }
            }
        )

    }
    // 누락 경고 다이얼로그
    if (showMissingDialog) {
        AlertDialog(
            onDismissRequest = { showMissingDialog = false },
            title = { Text("입력 누락") },
            text = { Text("${missingFields.joinToString(", ")} 을(를) 등록해주세요.") },
            confirmButton = {
                TextButton(onClick = { showMissingDialog = false }) {
                    Text("확인")
                }
            }
        )
    }
}

fun Uri.getDisplayName(context: Context): String {
    context.contentResolver.query(this, null, null, null, null)?.use { cursor ->
        val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (idx != -1 && cursor.moveToFirst()) {
            return cursor.getString(idx)
        }
    }
    return this.lastPathSegment ?: "unknown"
}

@Composable
fun SignUpButton(
    consentUri: Uri?,
    batchUri: Uri?,
    onFieldsEmpty: (missing: List<String>) -> Unit,
    onSignUpSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val apiService = NetworkClient.apiService

    // 업로드 중 표시 플래그
    var isUploading by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = {
                // 누락 파일 체크
                val missing = mutableListOf<String>()
                if (batchUri == null)   missing += "일괄 등록 양식"
                if (consentUri == null) missing += "보호자 동의서"
                if (missing.isNotEmpty()) {
                    onFieldsEmpty(missing)
                    return@Button
                }

                // 업로드 시작
                isUploading = true

                // Multipart 변환
                val batchPart   = prepareFilePartAllFile(context, batchUri!!,   "batch")
                val consentPart = prepareFilePartAllFile(context, consentUri!!, "consent")

                // 네트워크 호출
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = apiService.joinStudent(
                            consent = consentPart,
                            batch   = batchPart
                        )
                        withContext(Dispatchers.Main) {
                            isUploading = false
                            if (response.isSuccessful && response.body()?.flag?.flag == "1") {
                                Toast.makeText(
                                    context,
                                    "업로드가 완료되었습니다.\n파일 확인 후 아이디/비번 전달드립니다.",
                                    Toast.LENGTH_LONG
                                ).show()
                                onSignUpSuccess()
                            } else {
                                Toast.makeText(
                                    context,
                                    response.body()?.flag?.message ?: "업로드 실패",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        withContext(Dispatchers.Main) {
                            isUploading = false
                            Toast.makeText(
                                context,
                                "예외 발생: ${e.localizedMessage}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            },
            enabled = batchUri != null && consentUri != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(50)
        ) {
            Text("회원가입 완료", color = Color.White,  style = MaterialTheme.typography.body2)
        }

        // 업로드 중일 때는 버튼 위에 반투명 레이어 + 프로그레스
        if (isUploading) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}



fun prepareFilePartAllFile(
    context: Context,
    uri: Uri,
    partName: String
): MultipartBody.Part? {
    val contentResolver = context.contentResolver

    // 1) 원본 파일명 얻기
    val fileName = contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        cursor.getString(idx)
    } ?: "file_${System.currentTimeMillis()}"

    // 2) MIME 타입 얻기 (없으면 octet-stream)
    val mimeType = contentResolver.getType(uri) ?: "application/octet-stream"

    // 3) 캐시 디렉토리에 임시 파일 생성
    val tmpFile = File(context.cacheDir, fileName)
    try {
        contentResolver.openInputStream(uri)?.use { input ->
            tmpFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }

    // 4) RequestBody, MultipartPart 생성
    val requestFile = tmpFile
        .asRequestBody(mimeType.toMediaTypeOrNull())

    return MultipartBody.Part.createFormData(
        partName,
        fileName,
        requestFile
    )
}
