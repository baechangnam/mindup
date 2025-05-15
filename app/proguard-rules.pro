########################################
# Retrofit, OkHttp, Gson
########################################

# Retrofit 인터페이스 유지
-keep interface retrofit2.** { *; }
-keepclassmembers interface retrofit2.** {
    @retrofit2.http.* <methods>;
}

# OkHttp 클라이언트 유지
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**

# Gson 리플렉션 모델 유지
-keep class com.google.gson.** { *; }
-dontwarn com.google.gson.**
-keepattributes Signature
-keepattributes *Annotation*

########################################
# Coroutines adapter
########################################
-keep class com.jakewharton.retrofit2.adapter.kotlin.coroutines.** { *; }

########################################
# Jetpack Compose
########################################

# Compose 런타임 어노테이션 유지
-keep class androidx.compose.runtime.** { *; }
-dontwarn androidx.compose.runtime.**

# @Composable 어노테이션이 붙은 함수 유지
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}

########################################
# 일반 Kotlin/Java
########################################

# 직렬화(Parcelable) 어노테이션 유지
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

# 디버그 로깅 인터셉터 무시
-dontwarn okhttp3.logging.**