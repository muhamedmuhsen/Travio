package com.dev.home.presentation

import com.example.domain.model.destination.Destination
import ui.text.UiText

sealed interface HomeEvent {
    data class ShowErrorSnackbar(val message: UiText) : HomeEvent
    data class ShowSuccessSnackbar(val message: UiText) : HomeEvent
    data class NavigateToDestination(val id: String) : HomeEvent
    data object NavigateToSearch : HomeEvent
}

sealed interface HomeSection {
    data object Countries : HomeSection
    data object RecentlyViewed : HomeSection
    data object Recommended : HomeSection
    data object Nearby : HomeSection
}

sealed interface HomeAction {
    data object OnSearchClicked : HomeAction
    data class OnSearchQueryChanged(val query: String) : HomeAction
    data class OnDestinationClicked(val id: String) : HomeAction
    data class OnFavoriteClicked(val destination: Destination) : HomeAction
    data class OnRetrySection(val section: HomeSection) : HomeAction
}
