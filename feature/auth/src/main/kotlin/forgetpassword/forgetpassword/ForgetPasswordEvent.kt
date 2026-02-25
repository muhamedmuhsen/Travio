package com.example.feature.forgetpassword.forgetpassword

import ui.text.UiText

sealed interface ForgetPasswordEvent {
    data object NavigateToCodeScreen : ForgetPasswordEvent
    object OnBackClicked : ForgetPasswordEvent

    data class ShowError(val message: UiText) : ForgetPasswordEvent
}
