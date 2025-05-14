package apps.kr.mentalgrowth.common

import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import apps.kr.mentalgrowth.R
import apps.kr.mentalgrowth.model.ApiResponseModel
import apps.kr.mentalgrowth.ui.main.HeartType
import java.time.LocalDate
import java.time.YearMonth

object CommonUtil {

    //네트워크 연결확인
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
    }

    fun convertToCalendarMap(data: List<ApiResponseModel.HeartDay>, currentMonth: YearMonth): Map<Int, List<HeartType>> {
        return data
            .filter { heart ->
                // 현재 달 데이터만 필터링
                val date = LocalDate.parse(heart.date)
                YearMonth.from(date) == currentMonth
            }
            .associate { heart ->
                val date = LocalDate.parse(heart.date)
                val types = buildList {
                    if (heart.ATTEND > 0) add(HeartType.ATTEND)
                    if (heart.TOUCH > 0) add(HeartType.TOUCH)
                    if (heart.CHALLENGE > 0) add(HeartType.CHALLENGE)
                    if (heart.TALK > 0) add(HeartType.TALK)
                }
                date.dayOfMonth to types
            }
    }

    fun Color.toHex(): String {
        val r = (red * 255).toInt()
        val g = (green * 255).toInt()
        val b = (blue * 255).toInt()
        return String.format("#%02X%02X%02X", r, g, b)
    }

    fun getCurrentAppVersion(context: Context): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "1.0" // versionName이 null이면 기본값 "1.0" 반환
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            "1.0"
        }
    }



}
