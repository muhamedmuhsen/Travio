package com.dev.profile.editProfile

import ui.text.UiText

sealed interface EditProfileEvent {
    data object NavigateToProfile : EditProfileEvent
    data class ShowProfileError(val message: UiText) : EditProfileEvent
}

