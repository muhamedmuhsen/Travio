import com.google.firebase.appdistribution.gradle.firebaseAppDistribution
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

    fun normalizeApiBaseUrl(raw: String): String {
        val withProtocol = if (raw.startsWith("http://") || raw.startsWith("https://")) raw else "http://$raw"
        return withProtocol.removeSuffix("/api/").removeSuffix("/api").trimEnd('/') + "/api/"
    }

    fun normalizeImageBaseUrl(raw: String): String {
        val withProtocol = if (raw.startsWith("http://") || raw.startsWith("https://")) raw else "http://$raw"
        return withProtocol.removeSuffix("/api/").removeSuffix("/api").trimEnd('/')
    }

    productFlavors {
        create("emulator") {
            dimension = "environment"
            val emulatorBaseUrl = normalizeApiBaseUrl(readConfigValue("EMULATOR_BASE_URL", "http://10.0.2.2:5116/api/"))
            val emulatorImageBaseUrl = normalizeImageBaseUrl(readConfigValue("EMULATOR_IMAGE_BASE_URL", "http://10.0.2.2:5116"))

            buildConfigField("String", "ENVIRONMENT_NAME", "\"emulator\"")
            buildConfigField("String", "BASE_URL", "\"$emulatorBaseUrl\"")
            buildConfigField("String", "IMAGE_BASE_URL", "\"$emulatorImageBaseUrl\"")
            buildConfigField("boolean", "ENABLE_DEBUG_DIAGNOSTICS", "true")
            buildConfigField("boolean", "ENABLE_VERBOSE_NETWORK_LOGS", "true")
            buildConfigField("String", "APP_DISTRIBUTION_LABEL", "\"local\"")
        }

        create("deviceTester") {
            dimension = "environment"
            val testerBaseUrl = normalizeApiBaseUrl(readConfigValue("TESTER_DEVICE_BASE_URL", "http://tester.example.invalid:5116/api/"))
            val testerImageBaseUrl =
                normalizeImageBaseUrl(readConfigValue("TESTER_DEVICE_IMAGE_BASE_URL", "http://tester.example.invalid:5116"))

            buildConfigField("String", "ENVIRONMENT_NAME", "\"testerDevice\"")
            buildConfigField("String", "BASE_URL", "\"$testerBaseUrl\"")
            buildConfigField("String", "IMAGE_BASE_URL", "\"$testerImageBaseUrl\"")
            buildConfigField("boolean", "ENABLE_DEBUG_DIAGNOSTICS", "true")
            buildConfigField("boolean", "ENABLE_VERBOSE_NETWORK_LOGS", "true")
            buildConfigField("String", "APP_DISTRIBUTION_LABEL", "\"tester\"")
        }

        create("production") {
            dimension = "environment"
            val productionBaseUrl = normalizeApiBaseUrl(readConfigValue("PRODUCTION_BASE_URL", "http://api.example.invalid:5116/api/"))
            val productionImageBaseUrl =
                normalizeImageBaseUrl(readConfigValue("PRODUCTION_IMAGE_BASE_URL", "http://api.example.invalid:5116"))

            buildConfigField("String", "ENVIRONMENT_NAME", "\"production\"")
            buildConfigField("String", "BASE_URL", "\"$productionBaseUrl\"")
            buildConfigField("String", "IMAGE_BASE_URL", "\"$productionImageBaseUrl\"")
            buildConfigField("boolean", "ENABLE_DEBUG_DIAGNOSTICS", "false")
            buildConfigField("boolean", "ENABLE_VERBOSE_NETWORK_LOGS", "false")
            buildConfigField("String", "APP_DISTRIBUTION_LABEL", "\"production\"")
        }
    }

    defaultConfig {
        applicationId = "com.example.travio"
        minSdk = 29
        targetSdk = 36
        versionCode = 15
        versionName = "1.0"

        val googleWebClientId = localProperties.getProperty("GOOGLE_WEB_CLIENT_ID", "")
        buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", "\"$googleWebClientId\"")

        val stripePublishableKey =
            readConfigValue(
                "STRIPE_PUBLISHABLE_KEY",
                readConfigValue("stripe_publishable_key", "pk_test_replace_me"),
            )
        resValue("string", "stripe_publishable_key", stripePublishableKey)
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

secrets {
    propertiesFileName = "app/config/environment.secrets.properties"
    defaultPropertiesFileName = "app/config/environment.defaults.properties"
    // Keep endpoint selection owned by product flavors and explicit environment files.
    ignoreList +=
        listOf(
            "BASE_URL",
            "IMAGE_BASE_URL",
            "EMULATOR_BASE_URL",
            "EMULATOR_IMAGE_BASE_URL",
            "TESTER_DEVICE_BASE_URL",
            "TESTER_DEVICE_IMAGE_BASE_URL",
            "PRODUCTION_BASE_URL",
            "PRODUCTION_IMAGE_BASE_URL",
        )
}

androidComponents {
    beforeVariants(selector().all()) { variantBuilder ->
        val environmentFlavor = variantBuilder.productFlavors.find { it.first == "environment" }?.second

        val isCanonical =
            (environmentFlavor == "emulator" && variantBuilder.buildType == "debug") ||
                (environmentFlavor == "deviceTester" && variantBuilder.buildType == "release") ||
                (environmentFlavor == "production" && variantBuilder.buildType == "release")

        if (!isCanonical) {
            variantBuilder.enable = false
        }
    }
}

dependencies {
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
    implementation(project(":feature:hotel"))
    implementation(project(":feature:booking"))
    implementation(project(":feature:chat"))

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
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.androidx.core.splashscreen)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.coil.compose)
    implementation(libs.coil.svg)
    implementation(libs.okhttp)
    implementation(libs.timber)
    implementation(libs.stripe.android)
}

firebaseAppDistribution {
    artifactType = "APK"
    releaseNotes = "Tester device build"
    groups = providers.environmentVariable("FIREBASE_TESTER_GROUPS").orNull ?: ""
}

tasks.register("assembleTesterDeviceRelease") {
    group = "build"
    description = "Compatibility alias for tester distribution artifact assembly."
    dependsOn("assembleDeviceTesterRelease")
}

tasks.register("installTesterDeviceRelease") {
    group = "install"
    description = "Compatibility alias for tester distribution artifact install."
    dependsOn("installDeviceTesterRelease")
}

tasks.register("validateCanonicalReleaseEndpoints") {
    group = "verification"
    description = "Validates tester and production endpoint safety for release artifacts."

    doLast {
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

        val testerBaseUrl = readConfigValue("TESTER_DEVICE_BASE_URL", "http://tester.example.invalid:5116/api/")
        val testerImageBaseUrl = readConfigValue("TESTER_DEVICE_IMAGE_BASE_URL", "http://tester.example.invalid:5116")
        val productionBaseUrl = readConfigValue("PRODUCTION_BASE_URL", "http://api.example.invalid:5116/api/")
        val productionImageBaseUrl = readConfigValue("PRODUCTION_IMAGE_BASE_URL", "http://api.example.invalid:5116")
        val localhostAllowedForTester =
            readConfigValue("LOCALHOST_ALLOWED_FOR_TESTER", "false").equals("true", ignoreCase = true)

        val localhostMarkers = listOf("://localhost", "://127.0.0.1")
        val emulatorOnlyMarkers = listOf("://10.0.2.2")
        val releaseEndpoints =
            mapOf(
                "TESTER_DEVICE_BASE_URL" to testerBaseUrl,
                "TESTER_DEVICE_IMAGE_BASE_URL" to testerImageBaseUrl,
                "PRODUCTION_BASE_URL" to productionBaseUrl,
                "PRODUCTION_IMAGE_BASE_URL" to productionImageBaseUrl,
            )

        releaseEndpoints.forEach { (key, value) ->
            require(!value.contains("example.invalid")) { "$key must be configured for release artifacts" }
            require(emulatorOnlyMarkers.none { marker -> value.contains(marker) }) {
                "$key contains forbidden emulator endpoint: $value"
            }

            val isTesterEndpoint = key.startsWith("TESTER_DEVICE_")
            val shouldBlockLocalhost = !(isTesterEndpoint && localhostAllowedForTester)
            if (shouldBlockLocalhost) {
                require(localhostMarkers.none { marker -> value.contains(marker) }) {
                    "$key contains forbidden localhost endpoint: $value"
                }
            }
        }
    }
}
