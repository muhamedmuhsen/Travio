package com.example.feature.code

sealed interface CodeEvent {
    data object OnBackClicked : CodeEvent
    data class ShowError(val message: String) : CodeEvent
    data object OnSendAgain : CodeEvent
    data object NavigateToResetPassword : CodeEvent
}
