package com.example.feature.verifyEmail

import com.dev.utils.uitext.UiText

sealed interface VerifyEmailEvent {
    data object OnBackClicked : VerifyEmailEvent
    data object NavigateToSuccess : VerifyEmailEvent
    data class ShowError(val message: UiText) : VerifyEmailEvent
}
