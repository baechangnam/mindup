package apps.kr.mentalgrowth.ui.login

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import apps.kr.mentalgrowth.R
import apps.kr.mentalgrowth.network.NetworkClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TeacherSignUpScreen(navController: NavController,
    regions: List<String> = listOf(
        "서울특별시", "부산광역시", "대구광역시", "인천광역시", "광주광역시",
        "대전광역시", "울산광역시", "세종특별자치시", "경기도", "강원도",
        "충청북도", "충청남도", "전라북도", "전라남도", "경상북도", "경상남도", "제주특별자치도"
    ),
    grades: List<String> = listOf("3학년", "4학년", "5학년", "6학년"),
    onSearchSchool: (query: String) -> Unit,
    onSubmitCertificate: () -> Unit,
    onSignUpComplete: (
        region: String,
        school: String,
        grade: String,
        classNo: String,
        userId: String,
        password: String,
        name: String,
        nickName: String,
        agreed: Boolean
    ) -> Unit
) {
    var showMissingDialog by remember { mutableStateOf(false) }
    var missingFields by remember { mutableStateOf(listOf<String>()) }
    var certUri by remember { mutableStateOf<Uri?>(null) }
    var certName by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? ->
            uri?.let {
                certUri = it
                // 파일명 얻기
                context.contentResolver.query(it, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    cursor.moveToFirst()
                    certName = cursor.getString(nameIndex)
                }
              //  onAttachCertificate(it)
            }
        }
    )
    val privacyItems = listOf(
        "수집하는 개인정보 항목" to listOf(
            "필수: 성명, 학교(기관)명, 교원부문, 재직상태, 교육경력"
        ),
        "개인정보의 수집 및 이용 목적" to listOf(
            "회원자격 확인",
            "중복가입여부 확인"
        ),
        "개인정보의 보유 및 이용 기간" to listOf(
            "회원자격 및 중복가입여부 확인 후 즉시 파기"
        ),
        // 단일 문장도 List<String> 으로 래핑합니다.
        "서비스 이용 제한 안내" to listOf(
            "개인정보 제공에 동의하지 않으면 두근두근 마음성장 앱 서비스를 이용하실 수 없습니다."
        ),
        "서류인증 안내" to listOf(
            "나이스 재직증명서(PDF) 다운 후 파일 제출",
            "(서류 확인 후 7일 이내 승인)"
        )
    )


    // 다이얼로그 표시 플래그
    var showPrivacyDialog by remember { mutableStateOf(false) }
    // State
    var selectedRegion by remember { mutableStateOf(regions.firstOrNull().orEmpty()) }
    var regionMenuExpanded by remember { mutableStateOf(false) }

    var schoolQuery by remember { mutableStateOf("") }

    var selectedGrade by remember { mutableStateOf(grades.firstOrNull().orEmpty()) }
    var gradeMenuExpanded by remember { mutableStateOf(false) }

    var classNo by remember { mutableStateOf("") }
    var userId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var nickName by remember { mutableStateOf("") }

    var agreed by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 상단 체크 아이콘 + 타이틀
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {


            Icon(
                imageVector      = Icons.Default.Favorite,
                contentDescription = "하트",
                tint             = Color(0xFFE57373), // 원하시는 핑크 톤
                modifier         = Modifier.size(30.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "교사용 회원가입",
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(32.dp))

        // 지역
        ExposedDropdownMenuBox(
            expanded = regionMenuExpanded,
            onExpandedChange = { regionMenuExpanded = !regionMenuExpanded }
        ) {
            OutlinedTextField(
                value = selectedRegion,
                onValueChange = {},
                readOnly = true,
                label = { Text("지역") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(regionMenuExpanded) },
                modifier = Modifier.fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = regionMenuExpanded,
                onDismissRequest = { regionMenuExpanded = false }
            ) {
                regions.forEach { region ->
                    DropdownMenuItem(onClick = {
                        selectedRegion = region
                        regionMenuExpanded = false
                    }) {
                        Text(region)
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // 학교 검색
        OutlinedTextField(
            value = schoolQuery,
            onValueChange = {
                schoolQuery = it
                onSearchSchool(it)
            },
            label = { Text("학교") },
//            trailingIcon = {
//                Icon(
//                    imageVector    = Icons.Default.Search,
//                    contentDescription = "검색",
//                    modifier       = Modifier
//                        .size(24.dp)
//                        .clickable { onSearchSchool(schoolQuery) }
//                )
//            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        // 학년
        ExposedDropdownMenuBox(
            expanded = gradeMenuExpanded,
            onExpandedChange = { gradeMenuExpanded = !gradeMenuExpanded }
        ) {
            OutlinedTextField(
                value = selectedGrade,
                onValueChange = {},
                readOnly = true,
                label = { Text("학년") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(gradeMenuExpanded) },
                modifier = Modifier.fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = gradeMenuExpanded,
                onDismissRequest = { gradeMenuExpanded = false }
            ) {
                grades.forEach { grade ->
                    DropdownMenuItem(onClick = {
                        selectedGrade = grade
                        gradeMenuExpanded = false
                    }) {
                        Text(grade)
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // 학반
        OutlinedTextField(
            value = classNo,
            onValueChange = { classNo = it },
            label = { Text("학반") },
            modifier = Modifier.fillMaxWidth()    ,keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )

        Spacer(Modifier.height(16.dp))

        // ID 입력
        OutlinedTextField(
            value = userId,
            onValueChange = { userId = it },
            label = { Text("ID") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()

        )

        Spacer(Modifier.height(16.dp))

        // 비밀번호
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("비밀번호") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        // 이름
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("이름") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        // 닉네임
        OutlinedTextField(
            value = nickName,
            onValueChange = { nickName = it },
            label = { Text("닉네임") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        // 재직증명서 첨부 버튼
        Button(
            onClick = {
                // PDF 파일만 필터링
                pdfPickerLauncher.launch(arrayOf("application/pdf"))
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(50)
        ) {
            Text("재직증명서 첨부", color = Color.White,  style = MaterialTheme.typography.body2)
        }

        // 선택된 파일명 표시
        certName?.let { name ->
            Spacer(Modifier.height(8.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onSurface
            )
        }

        Spacer(Modifier.height(24.dp))

        // 개인정보 동의
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()  .clickable { showPrivacyDialog = true }  // 클릭 시 다이얼로그 오픈,


        ) {
            Checkbox(
                checked = agreed,
                onCheckedChange = null,  // 클릭 이벤트 없음
                colors = CheckboxDefaults.colors(
                    checkedColor           = MaterialTheme.colors.primary,
                    uncheckedColor         = MaterialTheme.colors.onSurface,
                   // disabledCheckedColor   = MaterialTheme.colors.primary,      // disabled 상태지만 체크됐을 때도 primary
                   // disabledUncheckedColor = MaterialTheme.colors.onSurface      // unchecked일 땐 onSurface 색
                )
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "개인정보 및 초상권 제공·수집·이용에 동의하십니까?",
                style = MaterialTheme.typography.body2
            )
        }

        Spacer(Modifier.height(24.dp))

        // 회원가입 완료
        TeacherSignUpButton(
            selectedRegion  = selectedRegion,
            schoolQuery     = schoolQuery,
            selectedGrade   = selectedGrade,
            classNo         = classNo,
            userId          = userId,
            password        = password,
            name            = name,
            nickName        = nickName,
            certificateUri  = certUri,
            agreed          = agreed,
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

    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("개인정보 수집·이용 안내") },
            text = {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .heightIn(max = 450.dp)
                        .padding(8.dp)
                ) {
                    privacyItems.forEach { (heading, details) ->
                        Text(
                            heading,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colors.primary
                        )
                        Spacer(Modifier.height(4.dp))
                        when (details) {
                            is List<*> -> details.filterIsInstance<String>().forEach { line ->
                                Text(
                                    "• $line",
                                    style = MaterialTheme.typography.body2,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                            else -> Text(
                                details.toString(),
                                style = MaterialTheme.typography.body2,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    agreed = true
                    showPrivacyDialog = false
                }) {
                    Text("동의")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPrivacyDialog = false
                }) {
                    Text("취소")
                }
            }
        )
    }

    // 누락 경고 다이얼로그
    if (showMissingDialog) {
        AlertDialog(
            onDismissRequest = { showMissingDialog = false },
            title = { Text("입력 누락") },
            text = { Text("${missingFields.joinToString(", ")} 을(를) 입력해주세요.") },
            confirmButton = {
                TextButton(onClick = { showMissingDialog = false }) {
                    Text("확인")
                }
            }
        )
    }
}

@Composable
fun TeacherSignUpButton(
    selectedRegion: String,
    schoolQuery: String,
    selectedGrade: String,
    classNo: String,
    userId: String,
    password: String,
    name: String,
    nickName: String,
    certificateUri: Uri?,
    agreed: Boolean,
    // 항목 누락 시 처리
    onFieldsEmpty: (missing: List<String>) -> Unit,
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
            val missing = mutableListOf<String>()
            if (selectedRegion.isBlank()) missing += "지역"
            if (schoolQuery.isBlank())  missing += "학교"
            if (selectedGrade.isBlank())missing += "학년"
            if (classNo.isBlank())      missing += "학반"
            if (userId.isBlank())       missing += "ID"
            if (password.isBlank())     missing += "비밀번호"
            if (name.isBlank())         missing += "이름"
            if (nickName.isBlank())     missing += "닉네임"
            if (certificateUri == null) missing += "재직증명서 첨부"
            if (!agreed)                missing += "개인정보 동의"

            if (missing.isNotEmpty()) {
                onFieldsEmpty(missing)
                return@Button
            }

            val gradeNumber = selectedGrade.filter { it.isDigit() }
            val gradePart   = createPartFromString(gradeNumber)

            // 2) 모든 값 준비
            val regionPart      = createPartFromString(selectedRegion)
            val schoolPart      = createPartFromString(schoolQuery)
            val classPart       = createPartFromString(classNo)
            val userIdPart      = createPartFromString(userId)
            val passwordPart    = createPartFromString(password)
            val namePart        = createPartFromString(name)
            val nickNamePart    = createPartFromString(nickName)
            val memIdPart       = createPartFromString(memId)
            val certificatePart = certificateUri?.let {
                prepareFilePart(context, it, "certificate")
            }

            // 3) 백그라운드에서 업로드
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = apiService.joinTeacher(
                        region      = regionPart,
                        school      = schoolPart,
                        grade       = gradePart,
                        classNo     = classPart,
                        userId      = userIdPart,
                        password    = passwordPart,
                        name        = namePart,
                        nickName    = nickNamePart,
                        memId       = memIdPart,
                        certificate = certificatePart
                    )
                    if (response.isSuccessful && response.body()?.flag?.flag == "1") {
                        // 메인 스레드에서 후속 처리
                        launch(Dispatchers.Main) {
                            Toast.makeText(context, "회원가입이 완료되었습니다.재직증명서 확인 후 로그인 가능합니다.", Toast.LENGTH_SHORT).show()
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
        Text("제출하기", color = Color.White,  style = MaterialTheme.typography.body1)
    }
}

// — 파일 파트 생성 (PDF 포함)
fun prepareFilePart(context: Context, uri: Uri, partName: String): MultipartBody.Part? {
    val fileName = "cert_${System.currentTimeMillis()}.pdf"
    val file = File(context.cacheDir, fileName)
    return try {
        context.contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        val requestFile = file.asRequestBody("application/pdf".toMediaTypeOrNull())
        MultipartBody.Part.createFormData(partName, file.name, requestFile)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun createPartFromString(descriptionString: String): RequestBody =
    RequestBody.create("text/plain".toMediaTypeOrNull(), descriptionString)