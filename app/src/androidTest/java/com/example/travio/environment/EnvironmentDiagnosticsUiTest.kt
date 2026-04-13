package com.example.travio.environment

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.travio.BuildConfig
import org.junit.Assume.assumeTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EnvironmentDiagnosticsUiTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun given_nonProductionVariant_when_renderPanel_then_showsEnvironmentAndHosts() {
        assumeTrue(BuildConfig.ENABLE_DEBUG_DIAGNOSTICS)

        val state = EnvironmentDiagnosticsState.fromBuildConfig()

        composeRule.setContent {
            EnvironmentDiagnosticsPanel(state = state)
        }

        composeRule.onNodeWithText("Environment: ${state.environmentName} (${state.buildFlavor}/${state.buildType})")
            .assertIsDisplayed()
        composeRule.onNodeWithText("API Host: ${state.apiHost}").assertIsDisplayed()
        composeRule.onNodeWithText("Image Host: ${state.imageHost}").assertIsDisplayed()
    }
}

