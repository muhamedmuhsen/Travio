package com.example.feature.newpassword

sealed interface NewPasswordEvent {
    data object NavigateToWelcome : NewPasswordEvent
    data object OnBackClicked : NewPasswordEvent
    data class ShowError(val message: String) : NewPasswordEvent
}

