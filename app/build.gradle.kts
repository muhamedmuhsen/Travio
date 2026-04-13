import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.firebase.appdistribution)
    alias(libs.plugins.google.gms.google.services)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.example.travio"
    compileSdk = 36

    flavorDimensions += "environment"

    val localProperties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localProperties.load(FileInputStream(localPropertiesFile))
    }

    val productionBaseUrl = localProperties.getProperty("BASE_URL", "http://10.0.2.2:5116/api/")

    productFlavors {
        create("production") {
            dimension = "environment"
            buildConfigField("String", "BASE_URL", "\"$productionBaseUrl\"")
        }
        create("localhost") {
            dimension = "environment"
            buildConfigField("String", "BASE_URL", "\"https://localhost:7219\"")
        }
    }

    defaultConfig {
        applicationId = "com.example.travio"
        minSdk = 29
        targetSdk = 36
        versionCode = 6
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val googleWebClientId = localProperties.getProperty("GOOGLE_WEB_CLIENT_ID", "")
        buildConfigField(
            "String",
            "GOOGLE_WEB_CLIENT_ID",
            "\"$googleWebClientId\"",
        )
    }

    buildTypes {
        debug {
            isDebuggable = true
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
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

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core modules
    implementation(project(":data"))
    implementation(project(":domain"))
    implementation(project(":core:common"))
    implementation(project(":core:network"))
    implementation(project(":core:designsystem"))
    implementation(project(":feature:auth"))
    implementation(project(":feature:home"))
    implementation(project(":feature:onboarding"))
    implementation(project(":feature:ai"))
    implementation(project(":feature:favorite"))
    implementation(project(":feature:profile"))
    implementation(project(":feature:community"))
    implementation(project(":feature:survey"))
    implementation(project(":feature:search"))
    implementation(project(":feature:destination"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.appcompat)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    testImplementation(libs.junit)

    // Splash
    implementation(libs.androidx.core.splashscreen)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Compose Navigation
    implementation(libs.androidx.navigation.compose)

    // Coil (image loading) + OkHttp for custom ImageLoader
    implementation(libs.coil.compose)
    implementation(libs.okhttp)

    // Timber logging library
    implementation(libs.timber)
}
