package com.example.feature.code

import com.dev.utils.uitext.UiText

sealed interface CodeEvent {
    data object OnBackClicked : CodeEvent
    data class ShowError(val message: UiText) : CodeEvent
    data object NavigateToResetPassword : CodeEvent
}
