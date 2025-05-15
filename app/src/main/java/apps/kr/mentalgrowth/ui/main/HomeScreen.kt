package apps.kr.mentalgrowth.ui.main

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.widget.TextView
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.NavController
import apps.kr.mentalgrowth.R
import androidx.lifecycle.viewmodel.compose.viewModel
import apps.kr.mentalgrowth.common.CommonView.MindBoardHeader
import apps.kr.mentalgrowth.model.ApiResponseModel
import apps.kr.mentalgrowth.network.NetworkClient
import apps.kr.mentalgrowth.ui.main.viewmodel.MainViewModel
import coil.compose.AsyncImage

//import apps.kr.mentalgrowth.ui.main.viewmodel.GolfCourseListViewModel

@Composable
fun HomeScreen( navController: NavController? = null,
                viewModel: MainViewModel = viewModel(),   onLogout: () -> Unit) {

    val boardList by viewModel.getBoardList.collectAsState()
    val boardVideoList by viewModel.getBoardVideo.collectAsState()
    val getBoardTitle by viewModel.getBoardTitle.collectAsState()
    val memberList by viewModel.memList.collectAsState()

    val hCount by viewModel.hCount.collectAsState()
    val eCount by viewModel.eCount.collectAsState()
    val aCount by viewModel.aCount.collectAsState()
    val rCount by viewModel.rCount.collectAsState()
    val tCount by viewModel.tCount.collectAsState()


    val nickName = LocalContext.current
        .getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        .getString("mem_nick", "닉네임")!!

    val class_group_id = LocalContext.current
        .getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        .getString("class_group_id", "")!!

    val mem_id = LocalContext.current
        .getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        .getString("mem_id", "")!!

    val mem_level = LocalContext.current
        .getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        .getString("mem_level", "")!!

    LaunchedEffect(class_group_id) {
        viewModel.getBoard("1", class_group_id,"")
        viewModel.getBoard("2", class_group_id,"0") //마음간판 1 , 마음영상0
        viewModel.getBoard("2", class_group_id,"1") //마음간판 1 , 마음영상0
        viewModel.getMyInfo(mem_id)

        viewModel.getBoardListTouchTotal("3",class_group_id,"",mem_id,"",mem_level)
    }

    Scaffold(
        topBar = {
            // ─── 상단 AppBar 영역 ─────────────────────────

            if (memberList.isNotEmpty()) {
                MemberHeader(memberList[0],onLogout,navController)
            }
        },

        content = { innerPadding ->
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
            ) {
                Spacer(Modifier.height(24.dp))

                // 1) 감정 차트
                EmotionChartRow(
                    listOf(hCount.toInt(), eCount.toInt(), aCount.toInt(), rCount.toInt(), tCount.toInt()),  // 예시 수치
                    heartColor = Color(0xFFF48FB1)
                )

                Spacer(Modifier.height(24.dp))

                // 2) 오늘 인사 메시지
                Surface(
                    color = Color(0xFFF48FB1),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    MarqueeTextView(
                        text = getBoardTitle.padEnd(30, '\u00A0'),
                        modifier = Modifier
                            .padding(vertical = 24.dp, horizontal = 20.dp),
                        textStyle = TextStyle(color = Color.White),
                        textSize = 20.sp
                    )
                }

                Spacer(Modifier.height(24.dp))

                // 3) 공지사항 & 마음영상 카드
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FeatureCard(
                        title = "공지사항",
                        icon = painterResource(R.drawable.warning),
                        onClick = { navController?.navigate("notice") },
                        modifier = Modifier.weight(1f),
                        listItems = boardList
                    )
                    FeatureCardWithGridThumbnails(

                        title = "마음영상",
                        icon = painterResource(R.drawable.video),
                        onClick = { navController?.navigate("mind_video")  },
                        modifier = Modifier.weight(1f),
                        listItems = boardVideoList

                    )
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    )




}


@Composable
fun MarqueeTextView(
    text: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle,
    textSize: TextUnit
) {
    AndroidView(
        factory = { ctx ->
            TextView(ctx).apply {
                // 1줄만 쓰고 마퀴 모드로
                setSingleLine(true)
                ellipsize = TextUtils.TruncateAt.MARQUEE
                marqueeRepeatLimit = -1   // 무한 반복
                isSelected = true         // 포커스 없어도 마퀴 돌게
                setTextColor(textStyle.color.toArgb())
                setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize.value)
                this.text = text
                typeface = ResourcesCompat.getFont(ctx, R.font.bold)
            }
        },
        update = { tv ->
            if (tv.text != text) tv.text = text
        },
        modifier = modifier
            .fillMaxWidth()
    )
}



@Composable
private fun EmotionChartRow(
    values: List<Int>,
    heartColor: Color,
    modifier: Modifier = Modifier
) {
    val heartLabels = listOf("H", "E", "A", "R", "T")
    val descriptions = listOf("알아봐요", "조절해요", "소통해요", "함께해요", "건강해요")
    val maxBarHeight = 120.dp
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. 막대그래프 Rowㅊㅇ
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            values.forEach { count ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    // (원한다면) count == 4 아이콘 삽입
                    if (count == 4) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Full Heart",
                            tint = Color.Red,
                            modifier = Modifier
                                .size(28.dp)
                                .padding(bottom = 4.dp)
                        )
                    }

                    // 4칸 박스: 항상 아래쪽부터 채워지고, 회색 테두리, 칸 사이 간격 없음
                    for (level in 4 downTo 1) {
                        Box(
                            modifier = Modifier
                                .width(30.dp)
                                .height(30.dp)
                                // 1dp 선 두께를 px 로 변환
                                .drawBehind {
                                    val stroke = 0.5.dp.toPx()
                                    drawRoundRect(
                                        color = Color.Gray,
                                        topLeft = Offset.Zero,
                                        size = size,
                                        cornerRadius = CornerRadius(4.dp.toPx()),
                                        style = Stroke(
                                            width = stroke,
                                            pathEffect = PathEffect.dashPathEffect(
                                                floatArrayOf(10f, 5f), // [대시 길이, 간격] in px
                                                0f
                                            )
                                        )
                                    )
                                }
                                .background(
                                    color = if (level <= count) heartColor else Color.Transparent,
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // ✅ 2. 하트 + 글자 Row → 여기에만 배경
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFEBEE), shape = RoundedCornerShape(12.dp))
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            heartLabels.forEachIndexed { index, label ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = heartColor,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = label,
                            color = Color.White,
                            fontSize = 14.sp,
                            style = MaterialTheme.typography.body1,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = descriptions.getOrNull(index) ?: "",
                        fontSize = 12.sp,
                        style = MaterialTheme.typography.body2,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}



@Composable
private fun FeatureCard(
    title: String,
    icon: Painter,
    onClick: () -> Unit,
    listItems: List<ApiResponseModel.Board>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(160.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // 상단 제목 + 아이콘
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.body2

                )

                Icon(
                    painter = icon,
                    contentDescription = title,

                    modifier = Modifier.size(17.dp),
                    tint = Color.Unspecified
                )
            }

            Spacer(modifier = Modifier.height(12.dp)) // ✅ 간격 추가

            if (listItems.isEmpty()) {
                Text(
                    text = "공지 없음",
                    style = MaterialTheme.typography.subtitle1,

                    color = Color.Gray
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listItems.take(2).forEach { item ->
                        Text(
                            text = "ㆍ${item.title}",
                            style = MaterialTheme.typography.subtitle1,
                            maxLines = 1
                        )
                    }
                }
            }

        }
    }
}



@Composable
private fun FeatureCardWithGridThumbnails(
    title: String,
    icon: Painter,
    onClick: () -> Unit,
    listItems: List<ApiResponseModel.Board>, // board.filename 사용
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(160.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // 상단 제목과 아이콘
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.body2
                )

                Icon(
                    painter = icon,
                    contentDescription = title,
                    modifier = Modifier.size(17.dp),
                    tint = Color.Unspecified
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ✅ 이미지 썸네일 그리드 (2x2)
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                val thumbnails = listItems.mapNotNull {
                    NetworkClient.BASE_URL_UPLOAD+it.filename

                }.take(4)

                for (row in thumbnails.chunked(2)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        row.forEach { imageUrl ->
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(10.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop,
                              //  placeholder = painterResource(id = R.drawable.warning),
                               // error = painterResource(id = R.drawable.warning),
                                onError = {
                                    Log.e("AsyncImage", "Image load failed: $imageUrl")
                                }
                            )
                        }

                        // 빈 칸 채우기
                        if (row.size < 2) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                }

                if (thumbnails.isEmpty()) {
                    Text("영상이 없습니다",   style = MaterialTheme.typography.body2,color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun MemberHeader(
    member: ApiResponseModel.MemberInfo,
    onLogout: () -> Unit,
    navController: NavController?
) {
    Surface(
        color = Color(0xFFF8BBD0),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. 왼쪽: 프로필 (weight = 1)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { navController?.navigate("profile") },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val imageUrl = member.mem_img
                    ?.takeIf { it.isNotBlank() }
                    ?.let { NetworkClient.BASE_URL_MEMBER + it }

                AsyncImage(
                    model = imageUrl ?: R.drawable.thumbnail_no_img1,
                    contentDescription = "프로필 이미지",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.height(2.dp))

                val shortSchool = member.school.substringBefore("초") + "초"
                Text(
                    text = "${member.mem_phone} ($shortSchool)",
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .widthIn(max = 80.dp)
                        .background(Color(0xAA000000), shape = RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                )
            }

            // 2. 가운데: wrapContentWidth + textAlign.Center
            Text(
                text = "두근두근 마음성장",
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(horizontal = 8.dp)
            )

            // 3. 오른쪽: 하트 2개 (weight = 1, End 정렬)
            Row(
                modifier = Modifier
                    .weight(1f),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = Color(0xFFE57373),
                    modifier = Modifier
                        .size(27.dp)
                        .rotate(30f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = Color(0xFFE57373),
                    modifier = Modifier
                        .size(14.dp)
                        .rotate(30f)
                )
            }
        }
    }
}
