package com.dev.home.presentation.flights

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import com.dev.home.components.FlightCard
import com.dev.home.components.previewFlightCard
import com.example.designsystem.theme.TravioTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class FlightCardCtaStateSmokeTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun givenCtaStates_whenRendered_thenOnlyLoadingStateShowsLoader() {
        val defaultCard = previewFlightCard(id = "cta-default")
        val pressedCard = previewFlightCard(id = "cta-pressed").copy(
            cta = FlightCardActionState.transition(
                current = defaultCard.cta,
                next = FlightCtaState.PRESSED
            )
        )
        val loadingCard = previewFlightCard(id = "cta-loading").copy(
            cta = FlightCardActionState.transition(
                current = defaultCard.cta,
                next = FlightCtaState.LOADING
            )
        )
        val disabledCard = previewFlightCard(id = "cta-disabled").copy(
            cta = FlightCardActionState.transition(
                current = defaultCard.cta,
                next = FlightCtaState.DISABLED
            )
        )

        composeRule.setContent {
            TravioTheme {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    FlightCard(content = defaultCard, onCardClick = {}, onCtaClick = {})
                    FlightCard(content = pressedCard, onCardClick = {}, onCtaClick = {})
                    FlightCard(content = loadingCard, onCardClick = {}, onCtaClick = {})
                    FlightCard(content = disabledCard, onCardClick = {}, onCtaClick = {})
                }
            }
        }

        composeRule.onNodeWithTag("flight_cta_cta-default").assertIsEnabled()
        composeRule.onNodeWithTag("flight_cta_cta-pressed").assertIsEnabled()
        composeRule.onNodeWithTag("flight_cta_cta-loading").assertIsNotEnabled()
        composeRule.onNodeWithTag("flight_cta_cta-disabled").assertIsNotEnabled()

        composeRule.onNodeWithTag("flight_cta_loading_cta-loading").assertIsDisplayed()
        assertEquals(0, composeRule.onAllNodesWithTag("flight_cta_loading_cta-default").fetchSemanticsNodes().size)
        assertEquals(0, composeRule.onAllNodesWithTag("flight_cta_loading_cta-pressed").fetchSemanticsNodes().size)
        assertEquals(0, composeRule.onAllNodesWithTag("flight_cta_loading_cta-disabled").fetchSemanticsNodes().size)
    }
}



