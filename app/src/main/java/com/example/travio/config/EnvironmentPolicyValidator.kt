package com.example.travio.config

import com.example.network.config.EndpointNormalizer

object EnvironmentPolicyValidator {
    fun validateProduction(
        baseUrl: String,
        imageBaseUrl: String
    ) {
        require(baseUrl.startsWith("https://")) { "Production BASE_URL must use https" }
        require(imageBaseUrl.startsWith("https://")) { "Production IMAGE_BASE_URL must use https" }

        require(!EndpointNormalizer.isLocalhostHost(baseUrl)) { "Production BASE_URL cannot use localhost" }
        require(!EndpointNormalizer.isEmulatorBridgeHost(baseUrl)) { "Production BASE_URL cannot use emulator bridge" }
        require(!EndpointNormalizer.isLocalhostHost(imageBaseUrl)) { "Production IMAGE_BASE_URL cannot use localhost" }
        require(!EndpointNormalizer.isEmulatorBridgeHost(imageBaseUrl)) { "Production IMAGE_BASE_URL cannot use emulator bridge" }
    }

    fun validateTesterDistribution(
        baseUrl: String,
        imageBaseUrl: String
    ) {
        require(!baseUrl.contains("example.invalid")) { "Tester BASE_URL must be configured" }
        require(!imageBaseUrl.contains("example.invalid")) { "Tester IMAGE_BASE_URL must be configured" }
        require(!EndpointNormalizer.isLocalhostHost(baseUrl)) { "Tester BASE_URL cannot use localhost" }
        require(!EndpointNormalizer.isEmulatorBridgeHost(baseUrl)) { "Tester BASE_URL cannot use emulator bridge" }
        require(!EndpointNormalizer.isLocalhostHost(imageBaseUrl)) { "Tester IMAGE_BASE_URL cannot use localhost" }
        require(!EndpointNormalizer.isEmulatorBridgeHost(imageBaseUrl)) { "Tester IMAGE_BASE_URL cannot use emulator bridge" }
    }
}
