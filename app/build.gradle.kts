import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.devtools.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "app.ynemreuslu.prayertimes"
    compileSdk = 35

    defaultConfig {
        applicationId = "app.ynemreuslu.prayertimes"
        minSdk = 23
        targetSdk = 35
        compileSdk = 35
        versionName = "1.0"

        // Api Keys
        val keystoreFile = project.rootProject.file("api_keys.properties")
        val properties = Properties()
        properties.load(keystoreFile.inputStream())
        val geminiApiKey = properties.getProperty("GEMINI_API_KEY") ?: ""
        val mapsApiKey = properties.getProperty("GOOGLE_MAPS_API_KEY") ?: ""
        buildConfigField("String", "GEMINI_API_KEY", "\"${geminiApiKey}\"")
        buildConfigField("String", "GOOGLE_MAPS_API_KEY", "\"${mapsApiKey}\"")
        manifestPlaceholders["GOOGLE_MAPS_API_KEY"] = geminiApiKey

    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
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
        buildConfig = true
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
    implementation (libs.androidx.material)

    // Gemini API
    implementation(libs.generativeai)

}


