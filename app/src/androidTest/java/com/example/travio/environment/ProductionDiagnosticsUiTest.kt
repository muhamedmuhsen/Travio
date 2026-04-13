package com.example.travio.environment

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assume.assumeTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProductionDiagnosticsUiTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun given_productionVariant_when_renderPanel_then_hidesHostDetails() {
        val state = EnvironmentDiagnosticsState.fromBuildConfig()
        assumeTrue(!state.showSensitiveHosts)

        composeRule.setContent {
            EnvironmentDiagnosticsPanel(state = state)
        }

        composeRule.onNodeWithText("Environment: ${state.environmentName} (${state.buildFlavor}/${state.buildType})")
            .assertIsDisplayed()
        composeRule.onAllNodesWithText("API Host: ${state.apiHost}").assertCountEquals(0)
        composeRule.onAllNodesWithText("Image Host: ${state.imageHost}").assertCountEquals(0)
    }
}


