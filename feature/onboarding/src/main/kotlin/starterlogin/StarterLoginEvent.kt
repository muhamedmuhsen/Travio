package com.example.feature.starterlogin

import ui.text.UiText

interface StarterLoginEvent {
    data class ShowAuthError(val message: UiText) : StarterLoginEvent
    data object NavigateToLogin : StarterLoginEvent
    data object NavigateToSignup : StarterLoginEvent
    data object GoogleSignIn : StarterLoginEvent
    data object FacebookSignIn : StarterLoginEvent
    data object NavigateToHome : StarterLoginEvent
}
