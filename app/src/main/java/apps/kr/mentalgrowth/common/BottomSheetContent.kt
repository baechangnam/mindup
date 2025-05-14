package apps.kr.mentalgrowth.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import apps.kr.mentalgrowth.model.ApiResponseModel

// 도우미 함수 : Int 값을 3자리마다 콤마 형식의 문자열로 변환
fun formatInt(value: Int): String = "%,d".format(value)

// 도우미 Composable : 테이블의 한 행을 출력
@Composable
fun TableRow(
    cells: List<String>,
    header: Boolean = false,
    rowBackground: Color = Color.Transparent
) {
    val textStyle = if (header) {
        MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold)
    } else {
        MaterialTheme.typography.body1
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(rowBackground)
    ) {
        for (cell in cells) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = cell, style = textStyle)
            }
        }
    }
}


@Composable
fun BottomSheetContent(event: ApiResponseModel.GolfCalendar) {
    // 라운딩 (회수)
    val count18 = event.cnt_eighteen
    val count9 = event.cnt_nine
    val roundTotal = count18 + count9

    // 캐디피 (수수료)
    val feeEighteen = event.fee_eighteen.toIntOrNull() ?: 0
    val feeNine = event.fee_nine.toIntOrNull() ?: 0
    val caddy18 = feeEighteen * count18
    val caddy9 = feeNine * count9
    val caddyTotal = caddy18 + caddy9

    // 오버피 (추가 비용)
    val over18 = event.over_eighteen * count18
    val over9 = event.over_nine * count9
    val overTotal = over18 + over9

    // 합계 (캐디피 + 오버피)
    val overall18 = caddy18 + over18
    val overall9 = caddy9 + over9
    val profitTotal = overall18 + overall9

    Column(modifier = Modifier.padding(16.dp)) {
        // 헤더 행
        // 헤더 행 (배경색 적용)
        TableRow(
            cells = listOf("구분", "18홀", "9홀", "합계"),
            header = true,
            rowBackground = Color.LightGray
        )
        Divider(color = Color.Gray, thickness = 1.dp)
        Spacer(modifier = Modifier.height(4.dp))

        // 라운딩 행
        TableRow(cells = listOf("라운딩", count18.toString(), count9.toString(), roundTotal.toString()))
        Divider(color = Color.Gray, thickness = 1.dp)
        Spacer(modifier = Modifier.height(4.dp))

        // 캐디피 행
        TableRow(
            cells = listOf(
                "캐디피",
                formatInt(caddy18),
                formatInt(caddy9),
                formatInt(caddyTotal)
            )
        )
        Divider(color = Color.Gray, thickness = 1.dp)
        Spacer(modifier = Modifier.height(4.dp))

        // 오버피 행
        TableRow(
            cells = listOf(
                "오버피",
                formatInt(over18),
                formatInt(over9),
                formatInt(overTotal)
            )
        )
        Divider(color = Color.Gray, thickness = 1.dp)
        Spacer(modifier = Modifier.height(4.dp))

        // 합계 행
        TableRow(
            cells = listOf(
                "합계",
                formatInt(overall18),
                formatInt(overall9),
                formatInt(profitTotal)
            )
        )
    }
}
