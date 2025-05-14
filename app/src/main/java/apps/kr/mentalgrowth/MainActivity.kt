package apps.kr.mentalgrowth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import apps.kr.mentalgrowth.common.TodayCaddyTheme
import apps.kr.mentalgrowth.navigation.AppNavigation


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TodayCaddyTheme {
                AppNavigation()  // 앱의 네비게이션이나 화면들을 구성하는 Composable 함수
            }
        }
    }
}