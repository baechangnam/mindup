package apps.kr.mentalgrowth.common


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ColorPicker(
    initialColor: Color,
    onColorSelected: (Color) -> Unit
) {
    val colors = listOf(
        Color(0xFFF44336), // Red
        Color(0xFFE91E63), // Pink
        Color(0xFF9C27B0), // Purple
        Color(0xFF673AB7), // Deep Purple
        Color(0xFF3F51B5), // Indigo
        Color(0xFF2196F3), // Blue
        Color(0xFF03A9F4), // Light Blue
        Color(0xFF00BCD4), // Cyan
        Color(0xFF009688), // Teal
        Color(0xFF4CAF50), // Green
        Color(0xFF8BC34A), // Light Green
        Color(0xFFCDDC39), // Lime
        Color(0xFFFFEB3B), // Yellow
        Color(0xFFFFC107), // Amber
        Color(0xFFFF9800), // Orange
        Color(0xFFFF5722), // Deep Orange
        Color(0xFF795548), // Brown
        Color(0xFF9E9E9E), // Gray
        Color(0xFF607D8B),  // Blue Gray
        Color(0xFF000000)  // Blue Gray
    )

    Column {
        // 예시로 4개씩 5행으로 배치 (총 20개 중 19개 사용 가능)
        colors.chunked(4).forEach { row ->
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                row.forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(color, shape = RoundedCornerShape(6.dp))
                            .clickable { onColorSelected(color) }
                    )
                }
            }
        }
    }
}

