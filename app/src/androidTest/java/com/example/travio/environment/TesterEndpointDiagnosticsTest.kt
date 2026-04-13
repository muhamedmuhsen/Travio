package com.example.travio.environment

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.network.config.EndpointNormalizer
import com.example.travio.BuildConfig
import org.junit.Assert.assertFalse
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TesterEndpointDiagnosticsTest {
    @Test
    fun given_testerVariant_when_readConfiguredHosts_then_forbidsLocalhostAndEmulatorBridge() {
        assumeTrue(BuildConfig.FLAVOR == "deviceTester")

        assertFalse(EndpointNormalizer.isLocalhostHost(BuildConfig.BASE_URL))
        assertFalse(EndpointNormalizer.isEmulatorBridgeHost(BuildConfig.BASE_URL))
        assertFalse(EndpointNormalizer.isLocalhostHost(BuildConfig.IMAGE_BASE_URL))
        assertFalse(EndpointNormalizer.isEmulatorBridgeHost(BuildConfig.IMAGE_BASE_URL))
    }
}

