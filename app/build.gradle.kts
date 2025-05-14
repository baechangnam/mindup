import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
}



// local.properties 로부터 프로퍼티 읽기
val localProperties = Properties().apply {
    val localPropsFile = rootProject.file("local.properties")
    if (localPropsFile.exists()) {
        load(localPropsFile.inputStream())
    }
}

android {
    namespace = "apps.kr.mentalgrowth"
    compileSdk = 34

    defaultConfig {
        applicationId = "apps.kr.mentalgrowth"
        minSdk = 26
        targetSdk = 34
        versionCode = 2
        versionName = "1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            type = "String",
            name = "BASE_URL",
            value = "\"${localProperties.getProperty("MINDUP_BASE_URL") ?: ""}\""
        )
        buildConfigField(
            type = "String",
            name = "BASE_URL_UPLOAD",
            value = "\"${localProperties.getProperty("MINDUP_BASE_URL_UPLOAD") ?: ""}\""
        )
        buildConfigField(
            type = "String",
            name = "BASE_URL_MEMBER",
            value = "\"${localProperties.getProperty("MINDUP_BASE_URL_MEMBER") ?: ""}\""
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        buildConfig = true
    }


    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation ("com.google.android.material:material:1.9.0")

    implementation ("io.coil-kt:coil-compose:2.2.2")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.compose.ui:ui:1.6.3")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.3")
    implementation ("androidx.media3:media3-exoplayer:1.1.1")
    implementation ("androidx.media3:media3-ui:1.1.1")
    implementation("androidx.compose.ui:ui-text:1.6.3")
    implementation("androidx.compose.ui:ui-text-android:1.6.3")
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    implementation("com.google.accompanist:accompanist-swiperefresh:0.30.1")

    // Coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.compose.runtime:runtime-android:1.7.8")

    // Tooling
    debugImplementation("androidx.compose.ui:ui-tooling:1.6.3")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    // Retrofit 라이브러리
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    // JSON 파싱을 위해 Gson 컨버터 추가 (원하는 다른 컨버터 사용 가능)
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    // 코루틴 사용 시 Retrofit과 함께 사용할 수 있는 adapter도 고려할 수 있습니다.
    implementation ("com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2")

    // 기타 필요한 라이브러리 (예: OkHttp)
    implementation ("com.squareup.okhttp3:okhttp:4.10.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.10.0")

    implementation ("com.google.accompanist:accompanist-pager:0.34.0") // 최신 버전 확인
    implementation ("com.google.accompanist:accompanist-pager-indicators:0.34.0")



}