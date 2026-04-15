package com.example.network.config

interface EnvironmentConfig {
    val environmentName: String
    val baseUrl: String
    val imageBaseUrl: String
    val enableDebugDiagnostics: Boolean
    val enableVerboseNetworkLogs: Boolean
    val appDistributionLabel: String

    val isProduction: Boolean
        get() = environmentName.equals("production", ignoreCase = true)
}
