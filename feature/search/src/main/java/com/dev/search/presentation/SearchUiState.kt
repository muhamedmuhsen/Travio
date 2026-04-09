package com.dev.search.presentation

import com.example.domain.model.destination.Destination
import com.example.domain.model.search.RecentSearch
import ui.state.UiState

data class SearchUiState(
    val query: String = "",
    val recentSearches: List<RecentSearch> = emptyList(),
    val searchResultsState: UiState<List<Destination>> = UiState.Idle
)
