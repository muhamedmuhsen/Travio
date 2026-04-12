package com.dev.favroite

import com.dev.favroite.components.SectionTab
import com.dev.utils.uitext.UiText
import com.example.domain.model.favorite.Place
import com.example.domain.model.favorite.Trip

sealed interface FavoritesTabUiState<out T> {
    data object Loading : FavoritesTabUiState<Nothing>

    data class Success<out T>(val items: List<T>) : FavoritesTabUiState<T>

    data object Empty : FavoritesTabUiState<Nothing>

    data class Error(val message: UiText) : FavoritesTabUiState<Nothing>
}

data class FavoritesPaginationState(
    val nextCursor: String? = null,
    val hasMore: Boolean = false,
    val isLoadingMore: Boolean = false,
    val loadMoreError: UiText? = null
)

data class FavoriteState(
    val selectedItem: Int = 1,
    val selectedTab: SectionTab = SectionTab.Destinations,
    val destinationsState: FavoritesTabUiState<Place> = FavoritesTabUiState.Loading,
    val tripsState: FavoritesTabUiState<Trip> = FavoritesTabUiState.Loading,
    val destinationsPagination: FavoritesPaginationState = FavoritesPaginationState(),
    val tripsPagination: FavoritesPaginationState = FavoritesPaginationState(),
    val loadedDestinations: List<Place> = emptyList(),
    val loadedTrips: List<Trip> = emptyList()
) {
    val totalFavoriteCount: Int get() = loadedDestinations.size + loadedTrips.size

    val displayedDestinations: List<Place>
        get() = if (selectedTab == SectionTab.Destinations) loadedDestinations else emptyList()

    val displayedTrips: List<Trip>
        get() = if (selectedTab == SectionTab.Trips) loadedTrips else emptyList()

    val currentTabState: FavoritesTabUiState<*>
        get() = when (selectedTab) {
            SectionTab.Destinations -> destinationsState
            SectionTab.Trips -> tripsState
        }

    val currentPaginationState: FavoritesPaginationState
        get() = when (selectedTab) {
            SectionTab.Destinations -> destinationsPagination
            SectionTab.Trips -> tripsPagination
        }
}
