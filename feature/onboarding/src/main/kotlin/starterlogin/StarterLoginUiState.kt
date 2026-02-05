package com.example.feature.starterlogin

import ui.state.UiState

data class StarterLoginUiState(
    val loginState: UiState<Unit> = UiState.Idle
)
