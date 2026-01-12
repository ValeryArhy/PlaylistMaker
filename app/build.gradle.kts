plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.plugin.parcelize")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin") version "2.5.3"
}

android {
    namespace = "com.example.playlistmaker"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.playlistmaker"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2025.12.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation("androidx.compose.runtime:runtime-livedata")


    implementation("androidx.lifecycle:lifecycle-viewmodel-compose")

    implementation("io.insert-koin:koin-androidx-compose:3.5.0")

    implementation("androidx.activity:activity-compose")

    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    // Kotlin
    implementation(libs.kotlinx.coroutines.android)

    // AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)
    implementation(libs.fragment.ktx)

    // Material
    implementation(libs.material)

    // Navigation
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)

    // Retrofit & Gson
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.gson)

    // Glide
    implementation(libs.glide)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)

    // Koin
    implementation(libs.koin.android)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation ("androidx.room:room-runtime:2.6.0")
    kapt ("androidx.room:room-compiler:2.6.0")
}
