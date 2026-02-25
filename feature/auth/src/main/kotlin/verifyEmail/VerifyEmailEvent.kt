package com.example.feature.verifyEmail

import ui.text.UiText

sealed interface VerifyEmailEvent {
    data object OnBackClicked : VerifyEmailEvent
    data object NavigateToSuccess : VerifyEmailEvent
    data class ShowError(val message: UiText) : VerifyEmailEvent
}
