package apps.kr.mentalgrowth.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp

@Composable
fun PartialStyleTextFieldExample() {
    // TextFieldValue를 사용하면 AnnotatedString과 selection 정보를 모두 관리할 수 있습니다.
    var textFieldValue by remember { mutableStateOf(TextFieldValue("여기에 텍스트를 입력하세요.")) }

    // 예시: Bold 스타일을 적용할 때 사용할 SpanStyle
    val boldStyle = SpanStyle(fontWeight = FontWeight.Bold)

    Column {
        // 스타일을 적용하는 버튼 (예: "굵게")
        Button(onClick = {
            // 현재 커서 위치부터 텍스트 끝까지 Bold 스타일 적용
            textFieldValue = applyStyleFromFocus(textFieldValue, boldStyle)
        }) {
            Text("굵게 적용")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 실제 텍스트 입력 필드: BasicTextField는 TextFieldValue를 사용하므로 AnnotatedString과 selection 정보를 확인할 수 있습니다.
        BasicTextField(
            value = textFieldValue,
            onValueChange = { newValue -> textFieldValue = newValue },
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 16.sp),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color(0xFFF0F0F0))
        )
    }
}

/**
 * 커서 위치(currentValue.selection.start)부터 텍스트 끝까지 주어진 스타일을 적용하는 함수.
 * 주의: 기존 스타일은 모두 덮어씌웁니다.
 */
fun applyStyleFromFocus(
    currentValue: TextFieldValue,
    styleToApply: SpanStyle
): TextFieldValue {
    val selectionStart = currentValue.selection.start.coerceAtLeast(0)
    val text = currentValue.annotatedString.text
    // 기존 텍스트의 앞부분은 그대로 두고, 커서 위치부터 끝까지 styleToApply를 적용
    val newAnnotated = buildAnnotatedString {
        append(text.substring(0, selectionStart))
        withStyle(style = styleToApply) {
            append(text.substring(selectionStart))
        }
    }
    // 선택 영역은 유지하거나 초기화할 수 있음(여기서는 단순히 유지)
    return currentValue.copy(annotatedString = newAnnotated)
}
