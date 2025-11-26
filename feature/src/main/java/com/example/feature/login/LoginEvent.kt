package com.example.feature.login

sealed interface LoginEvent {
    data object NavigateToHome : LoginEvent
    data object NavigateToSignup : LoginEvent
    data object NavigateToForgotPassword : LoginEvent
    data class ShowAuthError(val message: String) : LoginEvent
}