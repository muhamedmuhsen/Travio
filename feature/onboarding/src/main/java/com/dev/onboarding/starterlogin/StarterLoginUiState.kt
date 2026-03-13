package com.dev.onboarding.starterlogin

import com.dev.utils.uistate.UiState

data class StarterLoginUiState(
    val loginState: UiState<Unit> = UiState.Idle
)
