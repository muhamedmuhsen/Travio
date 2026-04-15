package com.dev.home.presentation

import com.dev.utils.uistate.UiState
import com.example.domain.model.destination.Country
import com.example.domain.model.destination.Destination

data class HomeUiState(
    val searchQuery: String = "",
    val countriesState: UiState<List<Country>> = UiState.Idle,
    val recommendedDestinationsState: UiState<List<Destination>> = UiState.Idle,
    val recentViewedDestinationsState: UiState<List<Destination>> = UiState.Idle,
    val nearbyDestinationsState: UiState<List<Destination>> = UiState.Idle,
    val favoriteIds: Set<Int> = emptySet(),
    val favoriteMutationInFlightIds: Set<Int> = emptySet()
)
