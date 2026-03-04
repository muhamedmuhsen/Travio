package com.dev.search.presentation

import ui.text.UiText

sealed interface SearchEvent {
    data class NavigateToDestination(val id: String) : SearchEvent
    data object NavigateBack : SearchEvent
    data class ShowErrorSnackbar(val message: UiText) : SearchEvent
}
