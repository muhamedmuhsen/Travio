package com.example.travio.environment

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.travio.BuildConfig
import org.junit.Assert.assertEquals
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TesterEnvironmentSmokeTest {
    @Test
    fun given_testerVariant_when_readEnvironmentName_then_matchesTesterDevice() {
        assumeTrue(BuildConfig.FLAVOR == "deviceTester")

        assertEquals("testerDevice", BuildConfig.ENVIRONMENT_NAME)
    }
}

