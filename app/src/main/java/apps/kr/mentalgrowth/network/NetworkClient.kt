package apps.kr.mentalgrowth.network

import apps.kr.mentalgrowth.model.ApiRequestModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkClient {

    private const val BASE_URL = "https://xxxx.mycafe24.com/"
    public const val BASE_URL_UPLOAD = "https://xxxx.mycafe24.com/upload/board/"
    public const val BASE_URL_MEMBER = "https://xxxx.mycafe24.com/upload/member/"


    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY  // BODY 레벨은 요청/응답 전체를 로그로 출력
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)
        .build()


    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    /**
     * 앱 버전을 서버에서 체크하는 suspend 함수
     * @param currentVersion 앱에서 관리하는 현재 버전 (예: "1.0")
     * @return 서버에 등록된 버전과 일치하면 true, 그렇지 않으면 false를 반환
     */
    suspend fun checkAppVersion(currentVersion: String): Boolean {
        return try {
            val response = apiService.getVersionInfo()
            // API 응답의 message 필드가 서버에서 등록한 버전입니다.
            response.flag.message == currentVersion
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


}