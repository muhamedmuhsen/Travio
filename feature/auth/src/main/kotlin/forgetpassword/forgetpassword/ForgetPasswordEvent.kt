package com.example.feature.forgetpassword

import ui.text.UiText

sealed interface ForgetPasswordEvent {
    data object NavigateToCodeScreen : ForgetPasswordEvent
    data object ContactUs : ForgetPasswordEvent
    object OnBackClicked : ForgetPasswordEvent

    data class ShowError(val message: UiText) : ForgetPasswordEvent
}


