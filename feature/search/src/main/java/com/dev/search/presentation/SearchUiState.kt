package com.dev.search.presentation

import com.example.domain.model.destination.Destination
import ui.state.UiState

data class SearchUiState(
    val query: String = "",
    val searchResultsState: UiState<List<Destination>> = UiState.Idle
)
