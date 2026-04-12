package com.dev.favroite

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.dev.favroite.components.Section
import com.dev.favroite.components.SectionTab
import com.example.designsystem.theme.TravioTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class FavoriteTabsSmokeTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun givenSection_whenRendered_thenShowsOnlyDestinationsAndTripsTabs() {
        composeRule.setContent {
            TravioTheme {
                Section(selectedTab = SectionTab.Destinations)
            }
        }

        composeRule.onNodeWithText("Destinations").assertIsDisplayed()
        composeRule.onNodeWithText("Trips").assertIsDisplayed()
    }

    @Test
    fun givenSection_whenTripsClicked_thenSelectionCallbackReturnsTrips() {
        var selected = SectionTab.Destinations

        composeRule.setContent {
            TravioTheme {
                Section(
                    selectedTab = selected,
                    onTabSelected = { selected = it }
                )
            }
        }

        composeRule.onNodeWithText("Trips").performClick()
        assertEquals(SectionTab.Trips, selected)
    }
}

