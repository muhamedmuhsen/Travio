package com.dev.profile.profile_

import ui.state.UiState

data class ProfileUiState(
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val profilePictureUrl: String? = null,
    val isDarkMode: Boolean = false,
    val isArabic: Boolean = false,
    val showLogoutDialog: Boolean = false,
    val profileUiState: UiState<Unit> = UiState.Idle
)
