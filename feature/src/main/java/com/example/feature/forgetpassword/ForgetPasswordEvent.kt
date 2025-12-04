package com.example.feature.forgetpassword

sealed interface ForgetPasswordEvent {
    data object NavigateToCodeScreen : ForgetPasswordEvent
    data object ContactUs : ForgetPasswordEvent
    object OnBackClicked : ForgetPasswordEvent

    data class ShowError(val message: String) : ForgetPasswordEvent
}


