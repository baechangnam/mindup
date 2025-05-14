// SplashScreen.kt
package apps.kr.mentalgrowth.ui.splash

import SplashViewModel
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import apps.kr.mentalgrowth.R
import apps.kr.mentalgrowth.common.CustomAlertDialog
import kotlinx.coroutines.delay
import kotlin.math.max
import kotlin.random.Random
// SplashScreen.kt … (생략)

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var playAnimation by remember { mutableStateOf(false) }
    var dialogState  by remember { mutableStateOf<SplashUiState?>(null) }

    // 하트 애니메이션 설정
    val heartCount    = 6
    val heartDelay    = 200L    // 0.2초 간격
    val heartDuration = 2500L   // 2.5초 이동
    val totalAnimTime = heartDelay * (heartCount - 1) + heartDuration
    val navDelay      = 1000L   // 마지막 하트 후 1초 대기

    LaunchedEffect(uiState) {
        if (uiState != SplashUiState.Loading) {
            dialogState   = uiState
            playAnimation = true
            // (≈3.5초) + 1초 = 4.5초 → 5초 이내
            delay(totalAnimTime + navDelay)
            when (uiState) {
                SplashUiState.NavigateToLogin -> navController.navigate("login") {
                    popUpTo("splash") { inclusive = true }
                }
                SplashUiState.NavigateToMain -> navController.navigate("main") {
                    popUpTo("splash") { inclusive = true }
                }
                else -> { /* dialog만 */ }
            }
        }
    }

    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
        Box(Modifier.fillMaxSize()) {
            if (!playAnimation) {
                Image(
                    painter            = painterResource(id = R.drawable.icon_big1),
                    contentDescription = "앱 로고",
                    modifier           = Modifier
                        .size(160.dp)
                        .align(Alignment.Center)
                )
            } else {
                val density   = LocalDensity.current
                val config    = LocalConfiguration.current
                val screenWpx = with(density) { config.screenWidthDp.dp.toPx() }
                val screenHpx = with(density) { config.screenHeightDp.dp.toPx() }
                val heartColor= MaterialTheme.colors.primaryVariant

                repeat(heartCount) { idx ->
                    // 화면 가로폭의 10%~90% 랜덤
                    val xFactor = 0.1f + Random.nextFloat() * 0.8f
                    // 화면 세로폭의 70%~90% 랜덤
                    val yFactor = 0.8f + (Random.nextFloat() - 0.5f) * 0.1f
                    HeartParticle(
                        delayMillis    = heartDelay * idx,
                        startXFactor   = xFactor,
                        startYFactor   = yFactor,
                        sizeDp         = Random.nextInt(24, 48).dp,
                        screenWidthPx  = screenWpx,
                        screenHeightPx = screenHpx,
                        heartColor     = heartColor,
                        animDuration   = heartDuration
                    )
                }

                Text(
                    "두근두근 마음성장",
                    style = MaterialTheme.typography.h3,
                    color = Color(0xFFF06292),

                    modifier = Modifier
                        .align(Alignment.Center)
                        .alpha( // 텍스트는 하트 애니 이후 바로 페이드인
                            animateFloatAsState(
                                targetValue = if (playAnimation) 1f else 0f,
                                animationSpec = tween(800)
                            ).value
                        )
                )
            }

            // … CustomAlertDialog 처리 생략 …
        }
    }
}

@Composable
fun HeartParticle(
    delayMillis: Long,
    startXFactor: Float,        // 화면 너비 대비 0f..1f
    startYFactor: Float,        // 화면 높이 대비 0f..1f
    sizeDp: Dp,
    screenWidthPx: Float,
    screenHeightPx: Float,
    heartColor: Color,
    animDuration: Long
) {
    val density  = LocalDensity.current
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(delayMillis)
        progress.animateTo(
            targetValue   = 1f,
            animationSpec = tween(durationMillis = animDuration.toInt(), easing = LinearEasing)
        )
    }

    val sizePx = with(density) { sizeDp.toPx() }
    val xPx    = startXFactor * (screenWidthPx - sizePx)
    val startYPx = startYFactor * (screenHeightPx - sizePx)
    val endYPx   = screenHeightPx * 0.2f

    // 20% 구간은 페이드인, 나머지 구간은 페이드아웃
    val alpha = run {
        val p = progress.value
        val fadeInPortion = 0.2f
        when {
            p <= fadeInPortion -> p / fadeInPortion
            else                -> max(0f, 1f - (p - fadeInPortion) / (1f - fadeInPortion))
        }
    }

    val yPx = startYPx + (endYPx - startYPx) * progress.value

    Canvas(
        modifier = Modifier
            .offset { IntOffset(xPx.toInt(), yPx.toInt()) }
            .size(sizeDp)
            .alpha(alpha)
    ) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w / 2f, h * 0.9f)
            cubicTo(w * 1.2f, h * 0.6f, w * 0.8f, h * 0.1f, w / 2f, h * 0.3f)
            cubicTo(w * 0.2f, h * 0.1f, w * -0.2f, h * 0.6f, w / 2f, h * 0.9f)
            close()
        }
        drawPath(path, color = heartColor)
    }
}
