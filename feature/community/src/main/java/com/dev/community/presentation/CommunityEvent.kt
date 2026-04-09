package com.dev.community.presentation

import com.dev.utils.uitext.UiText

sealed interface CommunityEvent {
    data class ShowSuccessSnackbar(val message: UiText) : CommunityEvent
    data class ShowErrorSnackbar(val message: UiText) : CommunityEvent
}
