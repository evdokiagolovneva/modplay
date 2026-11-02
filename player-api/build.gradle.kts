plugins {
    id("com.android.library")         // Делаем этот модуль Android-библиотекой (AAR)
    alias(libs.plugins.kotlin.compose)     // Подключаем Compose компилятор
    alias(libs.plugins.kotlin.serialization) // Для ModManifest (Json)
}

android {
    namespace = "com.example.moddyplayer.api"
    compileSdk = 37 // Используем стабильную версию SDK (35/37)

    defaultConfig {
        minSdk = 24 // Должен быть такой же или меньше, чем у плеера (у тебя 24)
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true // Включаем Compose, так как у тебя есть UiSlot с @Composable
    }
}

dependencies {
    // ВАЖНО: Используем 'api' вместо 'implementation'.
    // Это нужно для того, чтобы проект мода, подключив твой API, автоматически "увидел" типы Media3 и Compose.

    // Media3 (ExoPlayer) для работы с аудио-кадрами и эффектами
    api(libs.androidx.media3.exoplayer)
    api(libs.androidx.media3.session)

    // Jetpack Compose для создания UI-слотов внутри модов
    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.ui.graphics)
    api(libs.androidx.compose.material3)

    // Сериализация (для ModManifest)
    api(libs.kotlinx.serialization.json)

    // OkHttp (для сетевых запросов в API)
    api("com.squareup.okhttp3:okhttp:4.12.0")
    api("com.squareup.okhttp3:okhttp-coroutines:5.5.1")

    // Базовые утилиты Android
    api(libs.androidx.core.ktx)
}