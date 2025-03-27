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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        dataBinding = true
        viewBinding = true // ✅ Required!
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.material)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Navigation
    implementation(libs.androidx.navigation.compose)
    implementation (libs.accompanist.permissions)
    implementation (libs.play.services.location)

    // Lottie
    implementation(libs.lottie.compose)

    // Play Services
    implementation (libs.jetbrains.kotlinx.coroutines.play.services)

    // Retrofit
    implementation (libs.retrofit)
    implementation (libs.converter.gson)

    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore)

     // Maps
    implementation(libs.maps.compose)

    implementation(libs.androidx.core.ktx)

    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.constraintlayout.compose)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.appcompat) // ✅ Latest stable
    implementation(libs.firebase.firestore) // Or latest version
    implementation(libs.logging.interceptor) // ✅ latest stable

    //Room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    // skeleton
    implementation(libs.skeletonlayout)
}


