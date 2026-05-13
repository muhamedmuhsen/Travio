import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.example.data"
    compileSdk = 36

    flavorDimensions += "environment"

    buildFeatures {
        buildConfig = true
    }

    val localProperties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localProperties.load(FileInputStream(localPropertiesFile))
    }

    val secureProperties = Properties()
    val securePropertiesFile = rootProject.file("app/config/environment.secrets.properties")
    if (securePropertiesFile.exists()) {
        secureProperties.load(FileInputStream(securePropertiesFile))
    }

    val defaultsProperties = Properties()
    val defaultsPropertiesFile = rootProject.file("app/config/environment.defaults.properties")
    if (defaultsPropertiesFile.exists()) {
        defaultsProperties.load(FileInputStream(defaultsPropertiesFile))
    }

    fun readConfigValue(
        key: String,
        fallback: String,
    ): String {
        return providers.environmentVariable(key).orNull
            ?: secureProperties.getProperty(key)
            ?: defaultsProperties.getProperty(key)
            ?: localProperties.getProperty(key)
            ?: fallback
    }

    fun normalizeImageBaseUrl(raw: String): String {
        val withProtocol = if (raw.startsWith("http://") || raw.startsWith("https://")) raw else "http://$raw"
        return withProtocol.removeSuffix("/api/").removeSuffix("/api").trimEnd('/')
    }

    productFlavors {
        create("emulator") {
            dimension = "environment"
            val imageBaseUrl =
                normalizeImageBaseUrl(
                    readConfigValue("EMULATOR_IMAGE_BASE_URL", "http://10.0.2.2:5116"),
                )
            buildConfigField("String", "IMAGE_BASE_URL", "\"$imageBaseUrl\"")
        }

        create("localhost") {
            dimension = "environment"
            val imageBaseUrl =
                normalizeImageBaseUrl(
                    readConfigValue("LOCALHOST_IMAGE_BASE_URL", "http://localhost:5116"),
                )
            buildConfigField("String", "IMAGE_BASE_URL", "\"$imageBaseUrl\"")
        }

        create("deviceTester") {
            dimension = "environment"
            val imageBaseUrl =
                normalizeImageBaseUrl(
                    readConfigValue("TESTER_DEVICE_IMAGE_BASE_URL", "http://tester.example.invalid:5116"),
                )
            buildConfigField("String", "IMAGE_BASE_URL", "\"$imageBaseUrl\"")
        }

        create("production") {
            dimension = "environment"
            val imageBaseUrl =
                normalizeImageBaseUrl(
                    readConfigValue("PRODUCTION_IMAGE_BASE_URL", "http://api.example.invalid:5116"),
                )
            buildConfigField("String", "IMAGE_BASE_URL", "\"$imageBaseUrl\"")
        }
    }

    defaultConfig {
        minSdk = 29

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        val googleWebClientId = localProperties.getProperty("GOOGLE_WEB_CLIENT_ID", "")
        buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", "\"$googleWebClientId\"")
    }
    buildTypes {
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
}

dependencies {
    // Layer dependencies
    implementation(project(":domain"))
    implementation(project(":core:common"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))

    // Core dependencies
    implementation(libs.androidx.core.ktx)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)

    // Dependency Injection
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // DataStore Preferences
    implementation(libs.androidx.datastore.preferences)

    // Security & Auth
    implementation(libs.jwtdecode)
    implementation(libs.tink.android)

    // Credential Manager
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.facebook.login)

    // Testing
    testImplementation(project(":core:common"))
    testImplementation(project(":core:network"))
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)

    // Location
    implementation(libs.play.services.location)
    implementation(libs.kotlinx.coroutines.play.services)

    // Timber logging library
    implementation(libs.timber)
}
