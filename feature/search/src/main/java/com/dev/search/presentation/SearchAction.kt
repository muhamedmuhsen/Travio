package com.dev.search.presentation

import com.example.domain.model.destination.Destination

sealed interface SearchAction {
    data class OnQueryChanged(val query: String) : SearchAction
    data class OnDestinationClicked(val destination: Destination) : SearchAction
    data class OnRecentSearchClicked(val query: String) : SearchAction
    data class OnDeleteRecentSearch(val query: String) : SearchAction
    data object OnClearRecentSearches : SearchAction
    data object OnBackClicked : SearchAction
    data object OnClearQuery : SearchAction
    data object OnRetrySearch : SearchAction
}
