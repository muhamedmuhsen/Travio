package com.dev.home.presentation

import com.dev.home.components.CountryItem
import com.example.domain.model.destination.Destination
import ui.state.UiState

data class HomeUiState(
    val countriesState: UiState<List<CountryItem>> = UiState.Idle,
    val recommendedDestinationsState: UiState<List<Destination>> = UiState.Idle,
    val recentViewedDestinationsState: UiState<List<Destination>> = UiState.Idle,
    val nearbyDestinationsState: UiState<List<Destination>> = UiState.Idle
)
