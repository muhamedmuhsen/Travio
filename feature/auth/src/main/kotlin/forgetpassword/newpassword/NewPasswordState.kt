package com.example.feature.newpassword

import ui.state.UiState

data class NewPasswordState(
    val newPassword: String = "",
    val isPasswordsDoesnotMatch: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val confirmNewPassword: String = "",
    val isNewPasswordValid: Boolean = false,
    val newPasswordState: UiState<Unit> = UiState.Idle
)
