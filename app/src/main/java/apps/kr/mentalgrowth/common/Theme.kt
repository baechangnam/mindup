package apps.kr.mentalgrowth.common

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import apps.kr.mentalgrowth.R

// 기본 Light 테마 Colors 정의
val LightColorPalette = lightColors(
    primary = PrimaryColor,
    primaryVariant = PrimaryVariant,
    secondary = SecondaryColor
    // 필요에 따라 background, surface, error 등도 정의할 수 있음
)

@Composable
fun TodayCaddyTheme(content: @Composable () -> Unit) {

    val typography = MaterialTheme.typography.copy(
        h3 = TextStyle(
            fontFamily = MyFontFamily,
            fontWeight  = FontWeight.Bold,
            fontSize    = 32.sp
        ),
        h6 = TextStyle(
            fontFamily = MyFontFamily,
            fontWeight  = FontWeight.Bold,
            fontSize    = 20.sp,
                    color = Color.Black,
        ),
        h5= TextStyle(
            fontFamily = MyFontFamily,
            fontWeight  = FontWeight.ExtraBold,
            color = Color.Black,
            fontSize    = 23.sp
        ),
        body1 = TextStyle(
            fontFamily = MyFontFamily,
            fontWeight  = FontWeight.Normal,
            fontSize    = 16.sp
        ),
        body2 = TextStyle(
            fontFamily = MyFontFamily,
            fontWeight  = FontWeight.Normal,
            fontSize    =13.sp
        )
        ,
        subtitle1 = TextStyle(
            fontFamily = MyFontFamily,
            fontWeight  = FontWeight.Normal,
            fontSize    = 11.sp
        )
        ,
        caption = TextStyle(
            fontFamily = MyFontFamily,
            fontWeight  = FontWeight.Normal,
            fontSize    = 8.sp
        )
    )
    // 다크 테마 등 필요하면 조건문 사용 가능
    MaterialTheme(
        colors = LightColorPalette,
        typography = typography,
        content = content
    )
}

private val MyFontFamily = FontFamily(
    Font(R.font.mid, weight = FontWeight.Normal),
    Font(R.font.bold, weight = FontWeight.Bold),
    Font(R.font.ex, weight = FontWeight.ExtraBold)
)

