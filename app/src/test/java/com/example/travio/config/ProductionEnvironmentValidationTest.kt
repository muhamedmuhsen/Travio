package com.example.travio.config

import org.junit.Assert.assertThrows
import org.junit.Test

class ProductionEnvironmentValidationTest {
    @Test
    fun should_fail_when_productionBaseUrlUsesLocalhost() {
        assertThrows(IllegalArgumentException::class.java) {
            EnvironmentPolicyValidator.validateProduction(
                baseUrl = "https://localhost:7219/api/",
                imageBaseUrl = "https://cdn.example.com"
            )
        }
    }

    @Test
    fun should_fail_when_productionImageBaseUrlUsesEmulatorBridge() {
        assertThrows(IllegalArgumentException::class.java) {
            EnvironmentPolicyValidator.validateProduction(
                baseUrl = "https://api.example.com/api/",
                imageBaseUrl = "http://10.0.2.2:7219"
            )
        }
    }

    @Test
    fun should_pass_when_productionEndpointsAreHttpsAndPublicHosts() {
        EnvironmentPolicyValidator.validateProduction(
            baseUrl = "https://api.example.com/api/",
            imageBaseUrl = "https://cdn.example.com"
        )
    }
}

