package com.example.feature.verifyEmail

import ui.state.UiState

data class VerifyEmailState(
    val email: String = "",
    val code: String = "",
    val isCodeError: Boolean = false,
    val verificationState: UiState<Unit> = UiState.Idle,
    val timeLeft: Int = 0
)
