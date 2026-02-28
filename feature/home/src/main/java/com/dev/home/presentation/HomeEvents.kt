package com.dev.home.presentation

import ui.text.UiText

sealed interface HomeEvent {
    data class ShowErrorSnackbar(val message: UiText) : HomeEvent
    data class NavigateToDestination(val id: String) : HomeEvent
    data object NavigateToSearch : HomeEvent
}

sealed interface HomeAction {
    data object OnSearchClicked : HomeAction
    data class OnDestinationClicked(val id: String) : HomeAction
    data class OnFavoriteClicked(val id: String) : HomeAction
}
