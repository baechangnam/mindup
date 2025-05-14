package apps.kr.mentalgrowth.ui.main

import android.content.Context
import android.system.Os.close
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.drawText
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
fun MindTalkRecomScreen( navController: NavController? = null,
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

    val recommendedTitle = boardList.firstOrNull { it.noti_flag == "1" }?.title ?: "내용없음"


    LaunchedEffect(class_group_id) {
       // viewModel.getBoardTouch("4", class_group_id,"",mem_id,"today")
        viewModel.getBoardTalk("7", class_group_id, "", "", "")
    }

    val descriptions = listOf("알아봐요", "조절해요", "소통해요", "함께해요", "건강해요")
    var isScratched by remember { mutableStateOf(false) }
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
                    TitleWithHearts("마음추천")

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
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {

                Row(
                    modifier = Modifier
                        // 1dp 두께의 회색 테두리, 모서리 반경 8.dp
                        .border(0.5.dp, Color.Gray, RoundedCornerShape(12.dp))
                        // 내부 여백
                        .padding(8.dp)
                        // 부모 Column 내에서 가로 중앙에 배치
                        .align(Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                    // Row 내부 아이템들 가로 방향 중앙 정렬
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFFFB6C1), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text("마음추천", color = Color.White, fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.width(5.dp))

                    Text(
                        "오늘의 추천 마음으로 내가 어떤 하루를 보낼지 떠올려보세요.",
                        fontSize = 13.sp,
                        color = Color(0xFFDA2C43)
                    )

                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),               // 이 Box가 남은 공간을 모두 차지
                    contentAlignment = Alignment.Center  // 그 안에서 수직·수평 중앙
                ) {
                    ScratchCard(recommendedTitle)
                }





            }
        }
    )




}
@Composable
fun ScratchCard(recommendedTitle: String) {
    val scratchPoints = remember { mutableStateListOf<Offset>() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Text(
                text = "오늘의 추천 마음",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(24.dp))
            ) {
                // 실제 내용
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = recommendedTitle,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }

                // 스크래치 레이어
                Canvas(
                    modifier = Modifier
                        .matchParentSize()
                        .graphicsLayer {
                            compositingStrategy = CompositingStrategy.Offscreen
                        }
                        .pointerInput(Unit) {
                            detectDragGestures { change, _ ->
                                scratchPoints.add(change.position)
                            }
                        }
                ) {
                    drawRect(Color.Black)

                    drawIntoCanvas { canvas ->
                        val paint = android.graphics.Paint().apply {
                            isAntiAlias = true
                            xfermode = android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.CLEAR)
                        }

                        scratchPoints.forEach { offset ->
                            canvas.nativeCanvas.drawCircle(
                                offset.x,
                                offset.y,
                                36f,
                                paint
                            )
                        }
                    }
                }

                // 최초 메시지
                if (scratchPoints.isEmpty()) {
                    Text(
                        text = "여기를 긁으세요",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}
