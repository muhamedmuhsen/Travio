package com.example.travio.config

import com.example.network.config.EndpointNormalizer
import com.example.network.config.EnvironmentConfig
import com.example.travio.BuildConfig

data class AppEnvironmentConfig(
    override val environmentName: String,
    override val baseUrl: String,
    override val imageBaseUrl: String,
    override val enableDebugDiagnostics: Boolean,
    override val enableVerboseNetworkLogs: Boolean,
    override val appDistributionLabel: String
) : EnvironmentConfig {
    companion object {
        fun fromBuildConfig(): AppEnvironmentConfig {
            val mappedEnvironment = BuildVariantEnvironmentMapper.environmentForFlavor(BuildConfig.FLAVOR)

            val config = AppEnvironmentConfig(
                environmentName = BuildConfig.ENVIRONMENT_NAME,
                baseUrl = EndpointNormalizer.normalizeApiBaseUrl(BuildConfig.BASE_URL),
                imageBaseUrl = EndpointNormalizer.normalizeImageBaseUrl(BuildConfig.IMAGE_BASE_URL),
                enableDebugDiagnostics = BuildConfig.ENABLE_DEBUG_DIAGNOSTICS,
                enableVerboseNetworkLogs = BuildConfig.ENABLE_VERBOSE_NETWORK_LOGS,
                appDistributionLabel = BuildConfig.APP_DISTRIBUTION_LABEL
            )

            require(config.environmentName == mappedEnvironment.raw) {
                "Flavor '${BuildConfig.FLAVOR}' is inconsistent with ENVIRONMENT_NAME='${config.environmentName}'"
            }

            validateRequiredConfig(config)
            return config
        }

        private fun validateRequiredConfig(config: AppEnvironmentConfig) {
            if (config.environmentName == EnvironmentName.EMULATOR.raw) {
                return
            }

            require(config.baseUrl.isNotBlank()) { "BASE_URL is missing for ${config.environmentName}" }
            require(config.imageBaseUrl.isNotBlank()) { "IMAGE_BASE_URL is missing for ${config.environmentName}" }

            require(!config.baseUrl.contains("example.invalid")) {
                "BASE_URL must be provided for ${config.environmentName}"
            }
            require(!config.imageBaseUrl.contains("example.invalid")) {
                "IMAGE_BASE_URL must be provided for ${config.environmentName}"
            }

            if (config.environmentName == EnvironmentName.PRODUCTION.raw) {
                EnvironmentPolicyValidator.validateProduction(config.baseUrl, config.imageBaseUrl)
            }

            if (config.environmentName == EnvironmentName.TESTER_DEVICE.raw) {
                EnvironmentPolicyValidator.validateTesterDistribution(config.baseUrl, config.imageBaseUrl)
            }
        }
    }
}
