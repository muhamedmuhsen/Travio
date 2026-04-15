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

class FavoriteRemoveFlowSmokeTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun givenLastFavoriteRemoved_whenStateUpdated_thenEmptyStateIsDisplayed() {
        val destinationName = "Cappadocia"

        composeRule.setContent {
            TravioTheme {
                val favoritesState = remember {
                    mutableStateOf(
                        FavoriteState(
                            selectedTab = SectionTab.Destinations,
                            loadedDestinations = listOf(
                                Place(
                                    id = 101,
                                    name = destinationName,
                                    description = "Nevsehir",
                                    imageUrls = emptyList()
                                )
                            ),
                            destinationsState = FavoritesTabUiState.Success(
                                listOf(
                                    Place(
                                        id = 101,
                                        name = destinationName,
                                        description = "Nevsehir",
                                        imageUrls = emptyList()
                                    )
                                )
                            )
                        )
                    )
                }

                Column {
                    Button(
                        onClick = {
                            favoritesState.value = favoritesState.value.copy(
                                loadedDestinations = emptyList(),
                                destinationsState = FavoritesTabUiState.Empty
                            )
                        }
                    ) {
                        Text("Simulate Remove Favorite")
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

        composeRule.onNodeWithText("Simulate Remove Favorite").performClick()
        composeRule.onNodeWithText("No favorite destinations yet").assertIsDisplayed()
    }
}

