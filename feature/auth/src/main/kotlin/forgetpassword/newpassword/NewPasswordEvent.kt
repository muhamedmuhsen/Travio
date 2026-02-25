package com.example.feature.forgetpassword.newpassword

import ui.text.UiText

sealed interface NewPasswordEvent {
    data object NavigateToLogin : NewPasswordEvent
    data object OnClosedClicked : NewPasswordEvent
    data class ShowError(val message: UiText) : NewPasswordEvent
}
