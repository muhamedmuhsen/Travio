package com.example.feature.forgetpassword.code

import ui.state.UiState

data class CodeState(
    val code: String = "",
    val isCodeError: Boolean = false,
    val isCodeFilled: Boolean = false,
    val timeLeft: Int = 600,
    val codeState: UiState<Unit> = UiState.Idle
)
