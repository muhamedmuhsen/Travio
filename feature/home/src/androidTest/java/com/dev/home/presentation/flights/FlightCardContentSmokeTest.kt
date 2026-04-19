package com.dev.home.presentation.flights

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import com.dev.home.components.FlightsSection
import com.dev.home.components.previewFlightCard
import com.example.designsystem.theme.TravioTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class FlightCardContentSmokeTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun givenFlightsSection_whenRendered_thenCoreCardContentIsVisible() {
        composeRule.setContent {
            TravioTheme {
                FlightsSection(
                    flights = listOf(
                        previewFlightCard(id = "flight-1"),
                        previewFlightCard(id = "flight-2", amount = "529", status = "Boarding")
                    ),
                    onCardClick = {},
                    onCtaClick = {}
                )
            }
        }

        composeRule.onNodeWithText("Flights").assertIsDisplayed()
        composeRule.onNodeWithText("Virgin Atlantic").assertIsDisplayed()
        composeRule.onNodeWithText("LHR").assertIsDisplayed()
        composeRule.onNodeWithText("JFK").assertIsDisplayed()
        composeRule.onNodeWithText("Book Now").assertIsDisplayed()

        assertTrue(composeRule.onAllNodesWithText("Virgin Atlantic").fetchSemanticsNodes().size >= 2)
    }
}

