package com.dev.community.presentation

import com.dev.utils.uistate.UiState

data class ShareMomentUiState(
    val photoUri: String? = null,
    val location: String = "",
    val description: String = "",
    val submitState: UiState<Unit> = UiState.Idle
)

sealed interface ShareMomentEvent {
    data object PostCreated : ShareMomentEvent
    data object ShowPhotoRequired : ShareMomentEvent
    data object ShowLocationRequired : ShareMomentEvent
}
