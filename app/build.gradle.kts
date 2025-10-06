import org.jetbrains.kotlin.compose.compiler.gradle.ComposeFeatureFlag

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.plcoding.drawinginjetpackcompose"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.plcoding.drawinginjetpackcompose"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    }
}

composeCompiler {
    featureFlags.add(ComposeFeatureFlag.StrongSkipping)
}

dependencies {
    //ViewModel life-cycle scope
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.4")

    //Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    // Gson converter for JSON serialization/deserialization
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // OkHttp core (Retrofit uses OkHttp under the hood)
    implementation("com.squareup.okhttp3:okhttp:4.10.0")

    // OkHttp logging interceptor (for request/response logging)
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

    //Composable
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}