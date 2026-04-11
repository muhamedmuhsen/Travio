package com.dev.favroite

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.dev.favroite.components.SectionTab
import com.dev.utils.uitext.UiText
import com.example.designsystem.theme.TravioTheme
import org.junit.Rule
import org.junit.Test

class FavoriteStateRenderingTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun givenDestinationsEmpty_whenRendered_thenShowsDestinationEmptyCopy() {
        composeRule.setContent {
            TravioTheme {
                FavoriteContent(
                    state = FavoriteState(
                        selectedTab = SectionTab.Destinations,
                        destinationsState = FavoritesTabUiState.Empty
                    ),
                    onTabSelected = {},
                    onDeletePlace = {},
                    onDeleteTrip = {},
                    onRetryCurrentTab = {},
                    onLoadMoreCurrentTab = {},
                    onRetryLoadMoreCurrentTab = {},
                    onBottomBarItemSelected = {}
                )
            }
        }

        composeRule.onNodeWithText("No favorite destinations yet").assertIsDisplayed()
    }

    @Test
    fun givenTripsError_whenRendered_thenShowsTripErrorAndRetry() {
        composeRule.setContent {
            TravioTheme {
                FavoriteContent(
                    state = FavoriteState(
                        selectedTab = SectionTab.Trips,
                        tripsState = FavoritesTabUiState.Error(
                            UiText.DynamicString("Could not load favorite trips")
                        )
                    ),
                    onTabSelected = {},
                    onDeletePlace = {},
                    onDeleteTrip = {},
                    onRetryCurrentTab = {},
                    onLoadMoreCurrentTab = {},
                    onRetryLoadMoreCurrentTab = {},
                    onBottomBarItemSelected = {}
                )
            }
        }

        composeRule.onNodeWithText("Could not load favorite trips").assertIsDisplayed()
        composeRule.onNodeWithText("Retry").assertIsDisplayed()
    }
}


