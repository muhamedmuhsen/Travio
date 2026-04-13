package com.example.network.config

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EndpointNormalizerTest {
    @Test
    fun given_baseUrlWithoutApiSuffix_when_normalizeApiBaseUrl_then_appendsApiSuffix() {
        val result = EndpointNormalizer.normalizeApiBaseUrl("https://example.com")

        assertEquals("https://example.com/api/", result)
    }

    @Test
    fun given_baseUrlWithApiSuffix_when_normalizeApiBaseUrl_then_keepsSingleApiSuffix() {
        val result = EndpointNormalizer.normalizeApiBaseUrl("https://example.com/api/")

        assertEquals("https://example.com/api/", result)
    }

    @Test
    fun given_imageUrlWithApiSuffix_when_normalizeImageBaseUrl_then_removesApiSuffix() {
        val result = EndpointNormalizer.normalizeImageBaseUrl("https://example.com/api/")

        assertEquals("https://example.com", result)
    }

    @Test
    fun given_localhostUrl_when_isLocalhostHost_then_returnsTrue() {
        assertTrue(EndpointNormalizer.isLocalhostHost("https://localhost:7219/api/"))
        assertTrue(EndpointNormalizer.isLocalhostHost("http://127.0.0.1:8080/api/"))
        assertFalse(EndpointNormalizer.isLocalhostHost("https://example.com/api/"))
    }

    @Test
    fun given_emulatorBridgeUrl_when_isEmulatorBridgeHost_then_returnsTrue() {
        assertTrue(EndpointNormalizer.isEmulatorBridgeHost("http://10.0.2.2:7219/api/"))
        assertFalse(EndpointNormalizer.isEmulatorBridgeHost("https://example.com/api/"))
    }
}


