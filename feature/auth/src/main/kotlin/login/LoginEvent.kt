package com.example.feature.login

import com.dev.utils.uitext.UiText

sealed interface LoginEvent {
    data object NavigateToHome : LoginEvent
    data object NavigateToSurvey : LoginEvent
    data object NavigateToSignup : LoginEvent
    data object NavigateToForgotPassword : LoginEvent
    data object ContinueWithGoogle : LoginEvent
    data object ContinueWithFacebook : LoginEvent
    data class ShowAuthError(val message: UiText) : LoginEvent
}
