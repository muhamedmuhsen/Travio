package com.example.feature.starterlogin

interface StarterLoginEvent {
    data object NavigateToLogin : StarterLoginEvent
    data object NavigateToSignup : StarterLoginEvent
    data object GoogleSignIn : StarterLoginEvent
    data object FacebookSignIn : StarterLoginEvent
}