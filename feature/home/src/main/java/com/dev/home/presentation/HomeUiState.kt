package com.dev.home.presentation

import com.example.domain.model.destination.Country
import com.example.domain.model.destination.Destination
import ui.state.UiState

data class HomeUiState(
    val countriesState: UiState<List<Country>> = UiState.Idle,
    val recommendedDestinationsState: UiState<List<Destination>> = UiState.Idle,
    val recentViewedDestinationsState: UiState<List<Destination>> = UiState.Idle,
    val nearbyDestinationsState: UiState<List<Destination>> = UiState.Idle,
    val favoriteIds: Set<Int> = emptySet()
)
