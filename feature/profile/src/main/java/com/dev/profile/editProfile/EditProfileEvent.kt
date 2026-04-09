package com.dev.profile.editProfile

import com.dev.utils.uitext.UiText

sealed interface EditProfileEvent {
    data object NavigateToProfile : EditProfileEvent
    data class ShowProfileError(val message: UiText) : EditProfileEvent
}
