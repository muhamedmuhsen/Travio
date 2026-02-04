package com.dev.profile.profile_

import ui.text.UiText

sealed interface ProfileEvent {
    data object NavigateToEditProfile : ProfileEvent
    data object NavigateToChangeLanguage : ProfileEvent
    data object NavigateToLogin : ProfileEvent
    data class ToggleDarkMode(val isDarkMode: Boolean) : ProfileEvent
    data class ShowProfileError(val message: UiText) : ProfileEvent
}