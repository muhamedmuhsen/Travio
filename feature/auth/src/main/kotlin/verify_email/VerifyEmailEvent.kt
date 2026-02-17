package com.example.feature.verify_email

import ui.text.UiText

sealed interface VerifyEmailEvent {
    data class ShowError(val message: UiText) : VerifyEmailEvent
    data object NavigateToLogin : VerifyEmailEvent
}