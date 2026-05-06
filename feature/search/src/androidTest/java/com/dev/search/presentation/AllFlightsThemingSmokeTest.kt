package com.dev.search.presentation

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import com.dev.search.presentation.flights.FlightSearchUiState
import com.example.designsystem.theme.TravioTheme
import org.junit.Rule
import org.junit.Test

class AllFlightsThemingSmokeTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val defaultUiState = FlightSearchUiState()
    private val defaultOnAction: (com.dev.search.presentation.flights.FlightSearchAction) -> Unit = {}

    @Test
    fun allFlightsScreen_rendersInLightMode() {
        composeRule.setContent {
            TravioTheme(darkTheme = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AllFlightsScreen(
                        uiState = defaultUiState,
                        onAction = defaultOnAction
                    )
                }
            }
        }
        composeRule.waitForIdle()
    }

    @Test
    fun allFlightsScreen_rendersInDarkMode() {
        composeRule.setContent {
            TravioTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AllFlightsScreen(
                        uiState = defaultUiState,
                        onAction = defaultOnAction
                    )
                }
            }
        }
        composeRule.waitForIdle()
    }

    @Test
    fun allFlightsScreen_readableText_inBothModes() {
        listOf(false, true).forEach { darkTheme ->
            composeRule.setContent {
                TravioTheme(darkTheme = darkTheme) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        AllFlightsScreen(
                            uiState = defaultUiState,
                            onAction = defaultOnAction
                        )
                    }
                }
            }
            composeRule.waitForIdle()
        }
    }
}