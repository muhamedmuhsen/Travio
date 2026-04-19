package com.dev.home.presentation.flights

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.dev.home.components.FlightCard
import com.dev.home.components.previewFlightCard
import com.example.designsystem.theme.TravioTheme
import org.junit.Rule
import org.junit.Test

class FlightCardEdgeCaseSmokeTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun givenUnknownStatusAndMissingCity_whenRendered_thenFallbackTextAndSemanticsAreVisible() {
        val card = RawFlightCardPayload(
            id = "edge-fallback",
            airlineName = "Carrier",
            flightNumber = "CR123",
            statusLabel = null,
            departureTime = "10:15",
            durationText = "8H 10M",
            arrivalTime = "13:25",
            departureAirportCode = "LHR",
            departureCityName = null,
            arrivalAirportCode = "JFK",
            arrivalCityName = null,
            stopsText = "Non-stop",
            durationSummary = "Duration: 8h 10m",
            tripTypeSummary = "Total (Round Trip)",
            currencySymbol = "$",
            amountText = "489",
            qualifierText = "round trip"
        ).toFlightCardContent()

        composeRule.setContent {
            TravioTheme {
                FlightCard(content = card, onCardClick = {}, onCtaClick = {})
            }
        }

        composeRule.onNodeWithText("Unknown").assertIsDisplayed()
        composeRule.onNodeWithText("City unavailable").assertIsDisplayed()
    }

    @Test
    fun givenUnavailableFlight_whenRendered_thenCtaIsDisabled() {
        val unavailable = previewFlightCard(id = "edge-unavailable").copy(
            cta = FlightCardActionState.disabled("Book Now")
        )

        composeRule.setContent {
            TravioTheme {
                FlightCard(content = unavailable, onCardClick = {}, onCtaClick = {})
            }
        }

        composeRule.onNodeWithTag("flight_cta_edge-unavailable").assertIsNotEnabled()
    }
}

