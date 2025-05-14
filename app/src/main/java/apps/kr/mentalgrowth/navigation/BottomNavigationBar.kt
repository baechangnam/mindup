package apps.kr.mentalgrowth.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import apps.kr.mentalgrowth.R

data class BottomNavItem(
    val route: String,
    val iconRes: Int,
    val label: String
)
@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem("touch", R.drawable.touch, "마음터치"),
        BottomNavItem("challenge", R.drawable.challenge, "마음챌린지"),
        BottomNavItem("growth", R.drawable.growth, "마음성장"),
        BottomNavItem("talk", R.drawable.talk, "마음톡톡"),
        BottomNavItem("rank", R.drawable.rank, "마음랭킹")
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .height(80.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        items.forEachIndexed { index, item ->
            // 터치 그래프 내부면 모두 선택된 상태로 보기
            val isSelected = currentRoute?.startsWith(item.route) == true
            val targetRoute = if (item.route == "touch") "touch/main" else item.route



            if (index == 2) {
                // ❤️ 마음성장 (특수처리)
                val backgroundColor = if (isSelected) Color(0xFFFFE4EC) else Color.White
                val borderColor     = if (isSelected) Color(0xFFFF80AB) else Color(0xFFFFC0CB)
                val textColor       = if (isSelected) Color(0xFFFF4081) else Color.Black

                Column(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .clickable {
                            navController.navigate(targetRoute) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(color = backgroundColor, shape = CircleShape)
                            .border(width = 1.dp, color = borderColor, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("♥", fontSize = 25.sp, fontWeight = FontWeight.Bold, color = textColor)
                            Text("마음성장", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = textColor)
                        }
                    }
                }

            } else {
                // ✨ 다른 탭들
                val backgroundColor = if (isSelected) Color(0xFFFFE4EC) else Color.Transparent
                val borderColor     = if (isSelected) Color(0xFFFF80AB) else Color(0xFFFFC0CB)
                val textColor       = if (isSelected) Color(0xFFFF4081) else Color(0xFF000000)

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp)
                        .height(62.dp)
                        .background(color = backgroundColor, shape = RoundedCornerShape(12.dp))
                        .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(12.dp))
                        .clickable {
                            navController.navigate(targetRoute) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(Modifier.height(4.dp))

                    Image(
                        painter = painterResource(id = item.iconRes),
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(Modifier.height(2.dp))

                    Text(
                        text = item.label,
                        fontSize = MaterialTheme.typography.subtitle1.fontSize,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = textColor,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

