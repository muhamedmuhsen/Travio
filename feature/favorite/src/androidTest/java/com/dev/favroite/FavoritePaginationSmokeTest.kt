package com.dev.favroite

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.dev.favroite.components.SectionTab
import com.dev.utils.uitext.UiText
import com.example.designsystem.theme.TravioTheme
import com.example.domain.model.favorite.Place
import org.junit.Rule
import org.junit.Test

class FavoritePaginationSmokeTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun givenHasMore_whenFavoriteContentShown_thenLoadMoreActionVisible() {
        composeRule.setContent {
            TravioTheme {
                FavoriteContent(
                    state = FavoriteState(
                        selectedTab = SectionTab.Destinations,
                        loadedDestinations = listOf(
                            Place(id = 1, name = "Paris", description = "France", imageUrls = emptyList())
                        ),
                        destinationsState = FavoritesTabUiState.Success(
                            listOf(Place(id = 1, name = "Paris", description = "France", imageUrls = emptyList()))
                        ),
                        destinationsPagination = FavoritesPaginationState(
                            currentPageIndex = 1,
                            pageSize = 10,
                            totalCount = 20,
                            loadedCount = 1,
                            hasMore = true
                        )
                    ),
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

        composeRule.onNodeWithText("Load more").assertIsDisplayed()
    }

    @Test
    fun givenLoadMoreError_whenFavoriteContentShown_thenRetryActionVisible() {
        composeRule.setContent {
            TravioTheme {
                FavoriteContent(
                    state = FavoriteState(
                        selectedTab = SectionTab.Destinations,
                        loadedDestinations = listOf(
                            Place(id = 1, name = "Paris", description = "France", imageUrls = emptyList())
                        ),
                        destinationsState = FavoritesTabUiState.Success(
                            listOf(Place(id = 1, name = "Paris", description = "France", imageUrls = emptyList()))
                        ),
                        destinationsPagination = FavoritesPaginationState(
                            currentPageIndex = 1,
                            pageSize = 10,
                            totalCount = 20,
                            loadedCount = 1,
                            hasMore = true,
                            loadMoreError = UiText.DynamicString("Pagination failed")
                        )
                    ),
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

        composeRule.onNodeWithText("Retry").assertIsDisplayed()
    }
}

