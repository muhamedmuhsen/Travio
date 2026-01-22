package com.example.feature.code

import ui.text.UiText

sealed interface CodeEvent {
    data object OnBackClicked : CodeEvent
    data class ShowError(val message: UiText) : CodeEvent
    data object OnSendAgain : CodeEvent
    data object NavigateToResetPassword : CodeEvent
}
