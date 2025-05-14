package apps.kr.mentalgrowth.ui.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.system.Os.close
import android.util.Log
import android.widget.Toast
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
fun MindTalkGamecreen( navController: NavController? = null,
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
        viewModel.fetchBoardDetail("2421")
    }
    val context = LocalContext.current

    val board by viewModel.board.collectAsState()
    val gameUrl = board?.title?.takeIf { it.isNotBlank() }

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
                    TitleWithHearts("마음게임")

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
                    .verticalScroll(rememberScrollState())
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
                        Text("마음게임", color = Color.White, fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.width(5.dp))

                    Text(
                        "좋은 감정을 말하는 두더지를 두드리며, 스트레스를 풀어보세요.",
                        fontSize = 13.sp,
                        color = Color(0xFFDA2C43)
                    )
                }

                Spacer(modifier = Modifier.height(15.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "감정 두더지 게임으로\n스트레스를 풀어요",
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.DarkGray,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (!gameUrl.isNullOrBlank()) {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(gameUrl))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "링크 열기에 실패했습니다.", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, "링크가 없습니다.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("게임으로 이동하기",  style = MaterialTheme.typography.body2)

                    }
                }





            }
        }
    )




}

