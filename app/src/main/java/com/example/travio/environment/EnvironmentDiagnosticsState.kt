package com.example.travio.environment

import com.example.travio.BuildConfig
import java.net.URI

data class EnvironmentDiagnosticsState(
    val environmentName: String,
    val buildFlavor: String,
    val buildType: String,
    val showSensitiveHosts: Boolean,
    val apiHost: String,
    val imageHost: String
) {
    companion object {
        fun fromBuildConfig(): EnvironmentDiagnosticsState {
            return EnvironmentDiagnosticsState(
                environmentName = BuildConfig.ENVIRONMENT_NAME,
                buildFlavor = BuildConfig.FLAVOR,
                buildType = BuildConfig.BUILD_TYPE,
                showSensitiveHosts = BuildConfig.ENABLE_DEBUG_DIAGNOSTICS,
                apiHost = extractHost(BuildConfig.BASE_URL),
                imageHost = extractHost(BuildConfig.IMAGE_BASE_URL)
            )
        }

        private fun extractHost(rawUrl: String): String {
            return runCatching { URI(rawUrl).host ?: rawUrl }
                .getOrDefault(rawUrl)
        }
    }
}
