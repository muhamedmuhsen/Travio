package com.example.feature.signup

sealed interface SignupEvent {
    data object ContinueWithGoogle : SignupEvent
    data object ContinueWithFacebook : SignupEvent
    data object NavigateToLogin : SignupEvent
    data object onCreateAccountClicked : SignupEvent
    data class ShowAuthError(val message: String) : SignupEvent
    data object NavigateToHome : SignupEvent
}
