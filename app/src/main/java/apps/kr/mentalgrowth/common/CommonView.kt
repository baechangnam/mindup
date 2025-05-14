package apps.kr.mentalgrowth.common

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import apps.kr.mentalgrowth.R

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.LaunchedEffect

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate

import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.unit.dp



object CommonView {

    @Composable
    fun MindBoardHeader(title : String) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 왼쪽: 로고 영역

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Text("두근 두근", fontSize = 7.sp, color = Color.Black)

                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE57373)), // 연분홍 하트 배경
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "하트",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))


                Text("마음성장", fontSize = 7.sp, color = Color.Black)
            }


            // 오른쪽: 텍스트 + 하트 꾸밈
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.h5.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = Color(0xFFE57373),
                        modifier = Modifier.size(27.dp)  .rotate(30f)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = Color(0xFFE57373),
                        modifier = Modifier.size(14.dp)  .rotate(30f)
                    )
                }
            }
        }
    }

    @Composable
    fun TitleWithHearts(title: String) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Text("두근 두근", fontSize = 7.sp, color = Color.Black)

                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE57373)), // 연분홍 하트 배경
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "하트",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))


                Text("마음성장", fontSize = 7.sp, color = Color.Black)
            }

            Text(
                text = title,
                style = MaterialTheme.typography.h5.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = Color(0xFFE57373),
                modifier = Modifier.size(22.dp).rotate(30f)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = Color(0xFFE57373),
                modifier = Modifier.size(11.dp).rotate(30f)
            )
        }
    }


    @Composable
    fun NoDataIcon(
        modifier: Modifier = Modifier.size(25.dp)
    ) {
        Canvas(modifier = modifier) {
            // 원의 반지름 계산
            val radius = size.minDimension / 2

            // 원형 배경 그리기
            drawCircle(
                color = Color(0xFFE0E0E0), // 밝은 회색 배경
                radius = radius,
                center = center
            )

            // 패딩값 적용: X가 원 테두리와 충분히 떨어지도록 패딩을 설정합니다.
            val paddingFactor = 0.4f  // 반지름의 40%만큼 패딩 적용
            val padding = radius * paddingFactor

            // X를 그릴 수 있는 영역의 반지름
            val effectiveRadius = radius - padding

            // 선 두께 계산
            val strokeWidth = 4.dp.toPx()

            // X 표시 그리기: 첫 번째 대각선
            drawLine(
                color = Color(0xFF757575),
                start = Offset(center.x - effectiveRadius, center.y - effectiveRadius),
                end = Offset(center.x + effectiveRadius, center.y + effectiveRadius),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
            // X 표시 그리기: 두 번째 대각선
            drawLine(
                color = Color(0xFF757575),
                start = Offset(center.x + effectiveRadius, center.y - effectiveRadius),
                end = Offset(center.x - effectiveRadius, center.y + effectiveRadius),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
    }


    @Composable
    fun YearMonthPickerDialog(
        initialYear: Int,
        initialMonth: Int,
        onDismissRequest: () -> Unit,
        onDateSelected: (year: Int, month: Int) -> Unit
    ) {
        var selectedYear by remember { mutableStateOf(initialYear) }
        var selectedMonth by remember { mutableStateOf(initialMonth) }

        // 연도와 월 리스트 설정
        val years = (2020..2030).toList()
        val months = (1..12).toList()

        // 수평 스크롤 상태
        val scrollState = rememberScrollState()
        val density = LocalDensity.current

        // selectedYear가 변경될 때마다 해당 연도의 위치로 스크롤
        LaunchedEffect(selectedYear) {
            val index = years.indexOf(selectedYear)
            val itemWidthPx = with(density) { 60.dp.toPx() }
            scrollState.animateScrollTo((index * itemWidthPx).toInt())
        }

        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(
                    text = "년도 및 월 선택",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // 연도 선택 영역 (수평 스크롤 적용)
                    Text(
                        text = "년도 선택",
                        style = MaterialTheme.typography.subtitle1,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(scrollState),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        years.forEach { yearValue ->
                            val isSelected = yearValue == selectedYear
                            Card(
                                shape = RoundedCornerShape(8.dp),
                                backgroundColor = if (isSelected) MaterialTheme.colors.primary else Color.LightGray,
                                modifier = Modifier.clickable { selectedYear = yearValue }
                            ) {
                                Box(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = yearValue.toString(),
                                        style = MaterialTheme.typography.body1,
                                        color = if (isSelected) Color.White else Color.Black
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // 월 선택 영역 (2행 6개씩)
                    Text(
                        text = "월 선택",
                        style = MaterialTheme.typography.subtitle1,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            months.take(6).forEach { monthValue ->
                                val isSelected = monthValue == selectedMonth
                                Card(
                                    shape = RoundedCornerShape(8.dp),
                                    backgroundColor = if (isSelected) MaterialTheme.colors.primary else Color.LightGray,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedMonth = monthValue }
                                ) {
                                    Box(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${monthValue}월",
                                            style = MaterialTheme.typography.body1,
                                            color = if (isSelected) Color.White else Color.Black,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            months.drop(6).forEach { monthValue ->
                                val isSelected = monthValue == selectedMonth
                                Card(
                                    shape = RoundedCornerShape(8.dp),
                                    backgroundColor = if (isSelected) MaterialTheme.colors.primary else Color.LightGray,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedMonth = monthValue }
                                ) {
                                    Box(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${monthValue}월",
                                            style = MaterialTheme.typography.body1,
                                            color = if (isSelected) Color.White else Color.Black,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { onDateSelected(selectedYear, selectedMonth) }) {
                    Text(text = "확인")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(text = "취소")
                }
            }
        )
    }


}