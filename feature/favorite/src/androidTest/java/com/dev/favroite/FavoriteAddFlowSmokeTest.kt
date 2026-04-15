package com.dev.favroite

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.dev.favroite.components.SectionTab
import com.example.designsystem.theme.TravioTheme
import com.example.domain.model.favorite.Place
import org.junit.Rule
import org.junit.Test

class FavoriteAddFlowSmokeTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun givenHomeFavoriteAction_whenSynced_thenDestinationAppearsInFavoritesList() {
        val destinationName = "Cappadocia"

        composeRule.setContent {
            TravioTheme {
                val favoritesState = remember {
                    mutableStateOf(
                        FavoriteState(
                            selectedTab = SectionTab.Destinations,
                            destinationsState = FavoritesTabUiState.Empty,
                            loadedDestinations = emptyList()
                        )
                    )
                }

                Column {
                    Button(
                        onClick = {
                            val updatedItems = listOf(
                                Place(
                                    id = 101,
                                    name = destinationName,
                                    description = "Nevsehir",
                                    imageUrls = emptyList()
                                )
                            )
                            favoritesState.value = favoritesState.value.copy(
                                loadedDestinations = updatedItems,
                                destinationsState = FavoritesTabUiState.Success(updatedItems)
                            )
                        }
                    ) {
                        Text("Simulate Home Add Favorite")
                    }

                    FavoriteContent(
                        state = favoritesState.value,
                        onTabSelected = {},
                        onDeletePlace = {},
                        onDeleteTrip = {},
                        onRetryCurrentTab = {},
                        onLoadMoreCurrentTab = {},
                        onRetryLoadMoreCurrentTab = {},
                        onDestinationItemVisible = {},
                        onBottomBarItemSelected = {}
                    )
                }
            }
        }

        composeRule.onNodeWithText("Simulate Home Add Favorite").performClick()

        composeRule.onNodeWithText(destinationName).assertIsDisplayed()
    }
}


