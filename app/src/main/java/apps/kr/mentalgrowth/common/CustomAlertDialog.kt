package apps.kr.mentalgrowth.common

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import apps.kr.mentalgrowth.R

@Composable
fun CustomAlertDialog(
    title: String,
    message: String,
    confirmButtonText: String = "확인",
    onConfirm: () -> Unit,
    // dismissButtonText와 onDismiss는 선택사항으로 제공할 수 있습니다.
    dismissButtonText: String? = null,
    onDismiss: (() -> Unit)? = null,
    // 사용자가 다이얼로그 바깥을 터치했을 때 호출할 콜백 (보통 무시하거나 다이얼로그를 닫도록 구현)
    onDismissRequest: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.h6
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.body1
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor =  colorResource(id = R.color.colorMain),
                    contentColor = colorResource(id = R.color.white)
                )
            ) {
                Text(text = confirmButtonText)
            }
        },
        dismissButton = {
            if (dismissButtonText != null && onDismiss != null) {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor =  colorResource(id = R.color.colorGray),
                        contentColor = colorResource(id = R.color.colorWeakBlack)
                    )
                ) {
                    Text(text = dismissButtonText)
                }
            }
        }
    )
}
