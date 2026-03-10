package com.example.feature.signup

import com.dev.utils.uitext.UiText

sealed interface SignupEvent {
    data object NavigateToLogin : SignupEvent
    data class ShowAuthError(val message: UiText) : SignupEvent
    data object NavigateToHome : SignupEvent
    data object NavigateToVerifyEmail : SignupEvent
}
