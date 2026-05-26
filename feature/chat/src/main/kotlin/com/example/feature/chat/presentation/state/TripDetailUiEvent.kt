package com.example.feature.chat.presentation.state

import com.dev.utils.uitext.UiText

sealed interface TripDetailUiEvent {
    data class ShowToast(val message: UiText) : TripDetailUiEvent
}
