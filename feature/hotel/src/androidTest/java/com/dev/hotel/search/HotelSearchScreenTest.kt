package com.dev.hotel.search

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.dev.utils.uistate.UiState
import com.example.designsystem.theme.TravioTheme
import org.junit.Rule
import org.junit.Test

class HotelSearchScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun should_displaySearchForm_when_initiallyLoaded() {
        // Given
        val state = HotelSearchUiState(
            searchState = UiState.Idle,
            destination = "",
            isGuestSheetVisible = false
        )

        // When
        composeTestRule.setContent {
            TravioTheme {
                HotelSearchScreenContent(
                    state = state,
                    snackbarHostState = remember { SnackbarHostState() },
                    onAction = {}
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Find Hotels").assertIsDisplayed()
        composeTestRule.onNodeWithText("Destination").assertIsDisplayed()
        composeTestRule.onNodeWithText("Check-in").assertIsDisplayed()
        composeTestRule.onNodeWithText("Check-out").assertIsDisplayed()
        composeTestRule.onNodeWithText("Guests & Rooms").assertIsDisplayed()
        composeTestRule.onNodeWithText("Search Hotels").assertIsDisplayed()
    }

    @Test
    fun should_triggerSearchAction_when_inputsAreValidAndSearchClicked() {
        // Given
        val state = HotelSearchUiState(
            searchState = UiState.Idle,
            destination = "Paris",
            isGuestSheetVisible = false
        )
        var searchClicked = false

        // When
        composeTestRule.setContent {
            TravioTheme {
                HotelSearchScreenContent(
                    state = state,
                    snackbarHostState = remember { SnackbarHostState() },
                    onAction = { action ->
                        if (action is HotelSearchAction.SearchClicked) {
                            searchClicked = true
                        }
                    }
                )
            }
        }

        composeTestRule.onNodeWithText("Search Hotels").performClick()

        // Then
        assert(searchClicked)
    }
}
