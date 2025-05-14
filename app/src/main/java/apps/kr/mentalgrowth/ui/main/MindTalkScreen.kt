package apps.kr.mentalgrowth.ui.main

import android.content.Context
import android.system.Os.close
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import apps.kr.mentalgrowth.ui.main.viewmodel.MainViewModel
import coil.compose.AsyncImage



@Composable
fun MindTalkScreen( navController: NavController? = null,
                     viewModel: MainViewModel = viewModel()) {

    val boardList by viewModel.getBoardList.collectAsState()
    val boardVideoList by viewModel.getBoardVideo.collectAsState()
    val getBoardTitle by viewModel.getBoardTitle.collectAsState()
    val memberList by viewModel.memList.collectAsState()

    val nickName = LocalContext.current
        .getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        .getString("mem_nick", "닉네임")!!

    val class_group_id = LocalContext.current
        .getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        .getString("class_group_id", "")!!

    val mem_id = LocalContext.current
        .getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        .getString("mem_id", "")!!


    LaunchedEffect(class_group_id) {
       // viewModel.getBoardTouch("4", class_group_id,"",mem_id,"today")
    }

    val descriptions = listOf("알아봐요", "조절해요", "소통해요", "함께해요", "건강해요")

    Scaffold(
        topBar = {
            // ─── 상단 AppBar 영역 ─────────────────────────
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
                    TitleWithHearts("마음톡톡")

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
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,          // 세로 중앙
                horizontalAlignment = Alignment.CenterHorizontally  // 가로 중앙
            ) {
                // 옵션 사이 간격
                TalkBasket(
                    title = "나랑 톡톡",
                    backgroundPainter = painterResource(R.drawable.bb1),
                    color = Color(0xFfec35ac),
                    options = listOf("마음쓰기", "마음박스", "마음추천", "마음게임"),
                    navController = navController
                )
                Spacer(modifier = Modifier.height(24.dp))
                TalkBasket(
                    title = "친구랑 톡톡",
                    backgroundPainter = painterResource(R.drawable.bb2),
                    color = Color(0xFF87CEFA),
                    options = listOf("마음보기", "마음그림", "마음듣기", "마음상담"),
                    navController = navController
                )
            }

        }
    )




}

@Composable
fun TalkBasket(
    title: String,
    backgroundPainter: Painter,       // ← 동적 이미지
    color: Color,
    options: List<String>,
    navController: NavController? = null
) {
    // 1) 가로 폭만 고정, 높이는 이미지 비율에 따라 자동
    Box(
        modifier = Modifier
            .width(300.dp)
            .wrapContentHeight()   // 이미지 높이에 맞춤
    ) {
        // 2) 바구니 배경 이미지
        Image(
            painter = backgroundPainter,
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()       // 가로 꽉 채우기
                .height(240.dp)       // 세로 고정
        )

        // 3) 기존 컬럼 UI를 padding 으로 위치 조정
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp, start = 34.dp, end = 34.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .background(color, RoundedCornerShape(90))
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = title,
                    Modifier.padding(vertical = 10.dp),
                    color = Color.White,
                    style = MaterialTheme.typography.body1,

                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                options.chunked(2).forEach { row ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        row.forEach { item ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    // 높이를 고정해서 줄여줍니다
                                    .height(36.dp)
                                    // 배경 투명도 없이 진한 분홍
                                    .background(color, RoundedCornerShape(8.dp))
                                    .clickable {  when (item) {
                                        "마음쓰기"   -> navController?.navigate("talk_write")
                                        "마음박스"   -> navController?.navigate("talk_box")
                                        "마음추천"   -> navController?.navigate("talk_recomm")
                                        "마음게임"   -> navController?.navigate("talk_game")
                                        "마음보기"   -> navController?.navigate("talk_see")
                                        "마음그림"   -> navController?.navigate("talk_pic")
                                        "마음듣기"   -> navController?.navigate("talk_listen")
                                        "마음상담"   -> navController?.navigate("talk_consult")
                                    } }
                                    // 상하 패딩 줄이기
                                    .padding(vertical = 2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = item,
                                    color = Color.White,              // 진한 배경엔 흰색 텍스트가 잘 보여요
                                    style = MaterialTheme.typography.body2,              // 폰트 사이즈를 12sp로 줄였습니다
                                )
                            }

                        }
                    }
                }
            }
        }
    }
}

class BasketShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            moveTo(0f, size.height * 0.2f)
            quadraticBezierTo(
                size.width / 2, -size.height * 0.3f,
                size.width, size.height * 0.2f
            )
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        return Outline.Generic(path)
    }
}