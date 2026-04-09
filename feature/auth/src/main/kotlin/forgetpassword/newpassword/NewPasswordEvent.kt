package com.example.feature.forgetpassword.newpassword

import com.dev.utils.uitext.UiText

sealed interface NewPasswordEvent {
    data object NavigateToLogin : NewPasswordEvent
    data object OnClosedClicked : NewPasswordEvent
    data class ShowError(val message: UiText) : NewPasswordEvent
}
