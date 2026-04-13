package com.example.travio.config

import org.junit.Assert.assertEquals
import org.junit.Test

class ConfigSourcePrecedenceTest {
    @Test
    fun given_allValues_when_resolve_then_returnsCiValue() {
        val result = ConfigSourcePrecedenceResolver.resolve(
            ciValue = "ci",
            secureValue = "secure",
            localValue = "local",
            fallbackValue = "fallback"
        )

        assertEquals("ci", result)
    }

    @Test
    fun given_blankCiValue_when_resolve_then_returnsSecureValue() {
        val result = ConfigSourcePrecedenceResolver.resolve(
            ciValue = " ",
            secureValue = "secure",
            localValue = "local",
            fallbackValue = "fallback"
        )

        assertEquals("secure", result)
    }

    @Test
    fun given_onlyFallback_when_resolve_then_returnsFallback() {
        val result = ConfigSourcePrecedenceResolver.resolve(
            ciValue = null,
            secureValue = null,
            localValue = null,
            fallbackValue = "fallback"
        )

        assertEquals("fallback", result)
    }
}

