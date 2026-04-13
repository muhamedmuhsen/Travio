package com.example.travio.config

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class BuildVariantMappingTest {
    @Test
    fun given_emulatorFlavor_when_mapEnvironment_then_returnsEmulator() {
        val result = BuildVariantEnvironmentMapper.environmentForFlavor("emulator")

        assertEquals(EnvironmentName.EMULATOR, result)
    }

    @Test
    fun given_deviceTesterFlavor_when_mapEnvironment_then_returnsTesterDevice() {
        val result = BuildVariantEnvironmentMapper.environmentForFlavor("deviceTester")

        assertEquals(EnvironmentName.TESTER_DEVICE, result)
    }

    @Test
    fun given_invalidFlavor_when_mapEnvironment_then_throws() {
        assertThrows(IllegalStateException::class.java) {
            BuildVariantEnvironmentMapper.environmentForFlavor("unknown")
        }
    }
}

