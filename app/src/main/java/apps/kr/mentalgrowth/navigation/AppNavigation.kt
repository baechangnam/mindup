package apps.kr.mentalgrowth.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import apps.kr.mentalgrowth.ui.login.LoginScreen
import apps.kr.mentalgrowth.ui.login.StudentSignUpScreen
import apps.kr.mentalgrowth.ui.login.TeacherSignUpScreen
import apps.kr.mentalgrowth.ui.main.HomeScreen
import apps.kr.mentalgrowth.ui.main.MainScreen

import apps.kr.mentalgrowth.ui.splash.SplashScreen


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "splash") {

        composable("splash") { SplashScreen(navController = navController)}
        composable("login") {
            LoginScreen(navController = navController)
        }

        composable("main") {
            MainScreen(navController = navController)
        }


        composable("signup/teacher") {
            TeacherSignUpScreen(
                navController = navController,
                onSearchSchool = { query ->
                    // TODO: 학교 검색 로직 (API 호출 등)
                },
                onSubmitCertificate = {
                    // TODO: 재직증명서 제출 로직
                },
                onSignUpComplete = { region, school, grade, classNo, userId, password, name, nickName, agreed ->
                    // TODO: 실제 회원가입 API 호출
                    // 성공 시 메인 화면으로 이동
                    navController.navigate("login") {
                        popUpTo("signup/teacher") { inclusive = true }
                    }
                }
            )
        }

        composable("signup/student") {
            StudentSignUpScreen(
                navController = navController,
                onDownloadTemplate = { /* Intent로 URL 열기 또는 ViewModel 호출 */ },
                onDownloadConsent  = { /* Intent로 URL 열기 */ },
                onUploadBatch      = { uri ->
                    // ViewModel.uploadStudentBatch(uri)
                }
            )
        }






    }
}