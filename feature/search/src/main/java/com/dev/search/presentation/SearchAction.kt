package com.dev.search.presentation

import com.example.domain.model.destination.Destination

sealed interface SearchAction {
    data class OnQueryChanged(val query: String) : SearchAction
    data class OnDestinationClicked(val destination: Destination) : SearchAction
    data object OnBackClicked : SearchAction
    data object OnClearQuery : SearchAction
    data object OnRetrySearch : SearchAction
}
