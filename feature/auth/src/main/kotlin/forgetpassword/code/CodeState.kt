package com.example.feature.forgetpassword.code

import ui.state.UiState

data class CodeState(
    val code: String = "",
    val isCodeError: Boolean = false,
    val isCodeFilled: Boolean = false,
    val codeState: UiState<Unit> = UiState.Idle
)
