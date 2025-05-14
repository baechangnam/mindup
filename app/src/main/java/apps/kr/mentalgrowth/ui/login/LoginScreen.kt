package apps.kr.mentalgrowth.ui.login

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import apps.kr.mentalgrowth.R


@Composable
fun LoginScreen(viewModel: LoginViewModel = viewModel(), navController: NavController
) {
    // State
    var userId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val activity = LocalContext.current as Activity

    // ViewModel의 UI 상태를 Compose에서 구독
    val uiState by viewModel.uiState.collectAsState()
    // 에러 다이얼로그 표시 여부 상태
    val showErrorDialog = remember { mutableStateOf(false) }


    var showSignUpDialog by remember { mutableStateOf(false) }
    var showEmptyDialog by remember { mutableStateOf(false) }

    if (showEmptyDialog) {
        AlertDialog(
            onDismissRequest = { showEmptyDialog = false },
            title   = { Text("입력 오류") },
            text    = { Text("아이디와 비밀번호를 모두 입력해주세요.") },
            confirmButton = {
                Button(onClick = { showEmptyDialog = false }) {
                    Text("확인")
                }
            }
        )
    }

    // 로그인 성공 시 메인 화면으로 이동
    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            val sharedPref = activity.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
            sharedPref.edit().apply {
                putBoolean("isLoggedIn", true)
                putString("mem_id", uiState.mem_id ?: "")
                putString("mem_level", uiState.mem_level ?: "")
                putString("mem_nick", uiState.nickname ?: "")

                putString("class_group_id", uiState.class_group_id ?: "")

                Log.d("myLog" , "class_group_id "   + uiState.class_group_id)
                Log.d("myLog" , "mem_level "   + uiState.mem_level)
                apply()
            }
            navController.navigate("main") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage != null) {
            showErrorDialog.value = true
        }
    }

    if (showErrorDialog.value) {
        androidx.compose.material.AlertDialog(
            onDismissRequest = { showErrorDialog.value = false
                viewModel.clearError()     },
            title = { Text(text = "로그인 실패",  style = MaterialTheme.typography.body2) },
            text = { Text(text = uiState.errorMessage ?: "알 수 없는 오류",  style = MaterialTheme.typography.body2) },
            confirmButton = {
                Button(onClick = { showErrorDialog.value = false
                    viewModel.clearError()     }) {
                    Text("확인",  style = MaterialTheme.typography.body2)

                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1) 로고 아이콘

        Image(
            painter = painterResource(id = R.drawable.icon_big1), // 프로젝트에 넣은 로고
            contentDescription = "앱 로고",
            modifier = Modifier.size(140.dp)
        )

        // 2) 타이틀
        Text(
            text = "마음성장",
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 3) 아이디 입력
        OutlinedTextField(
            value = userId,
            onValueChange = { userId = it },
            label = { Text("아이디",  style = MaterialTheme.typography.body2) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 4) 비밀번호 입력
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("비밀번호",  style = MaterialTheme.typography.body2) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 5) 버튼 둘을 가로로 배치
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    if (userId.isBlank() || password.isBlank()) {
                        showEmptyDialog = true
                    } else {
                        viewModel.loginWithApi(userId.trim(), password)
                    }
                },
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                Text("로그인",  style = MaterialTheme.typography.body2)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {     showSignUpDialog = true },
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                Text("회원가입",  style = MaterialTheme.typography.body2)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 6) 하단 설명
        Text(
            text = "사회정서교육을 위한 학생 교육용 앱",
            style = MaterialTheme.typography.body2,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(250.dp))
    }

    if (showSignUpDialog) {
        AlertDialog(
            onDismissRequest = { showSignUpDialog = false },
            title = { Text("회원가입 유형 선택",  style = MaterialTheme.typography.body1) },
            text = { Text("교사용으로 가입하시겠습니까? 학생용으로 가입하시겠습니까?",  style = MaterialTheme.typography.body2) },
            buttons = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            showSignUpDialog = false
                            navController.navigate("signup/teacher")
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("교사용 가입",  style = MaterialTheme.typography.body2)
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            showSignUpDialog = false
                            navController.navigate("signup/student")

                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("학생용 가입",  style = MaterialTheme.typography.body2)
                    }
                }
            }
        )
    }
}



