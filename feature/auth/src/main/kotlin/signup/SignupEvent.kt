package com.example.feature.signup

import ui.text.UiText

sealed interface SignupEvent {
    data object NavigateToLogin : SignupEvent
    data class ShowAuthError(val message: UiText) : SignupEvent
    data object NavigateToHome : SignupEvent
}
