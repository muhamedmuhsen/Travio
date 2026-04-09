package com.dev.search.presentation

import com.dev.utils.uistate.UiState
import com.example.domain.model.destination.Destination
import com.example.domain.model.search.RecentSearch

data class SearchUiState(
    val query: String = "",
    val recentSearches: List<RecentSearch> = emptyList(),
    val searchResultsState: UiState<List<Destination>> = UiState.Idle
)
