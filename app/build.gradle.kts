plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.devtools.ksp)
    alias(libs.plugins.hilt.android)
    id("kotlin-kapt") // ✅ Simple, works without version conflict
    id("kotlin-parcelize")
}

android {
    namespace = "com.drmiaji.prayertimes"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.drmiaji.prayertimes"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        dataBinding = true
        viewBinding = true // ✅ Required!
    }
}

dependencies {

    // ✅ Core & Foundation
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.constraintlayout.compose)

    // ✅ Jetpack Compose
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.material)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // ✅ Hilt (Dependency Injection)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // ✅ Navigation & Permissions
    implementation(libs.androidx.navigation.compose)
    implementation(libs.accompanist.permissions)

    // ✅ Location & Maps
    implementation(libs.play.services.location)
    implementation(libs.maps.compose)

    // ✅ Network - Retrofit & Logging
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    // ✅ Lottie Animations
    implementation(libs.lottie.compose)

    // ✅ Coroutines & Play Services
    implementation(libs.jetbrains.kotlinx.coroutines.play.services)

    // ✅ Firebase
    implementation(libs.firebase.firestore)

    // ✅ DataStore
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.datastore.preferences)

    // ✅ Room Database
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    // ✅ Skeleton Layout (Shimmer loading)
    implementation(libs.skeletonlayout)
}


