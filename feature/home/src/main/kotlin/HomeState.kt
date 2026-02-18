package com.example.feature.home

import ui.state.UiState

data class HomeState(
    val selectedItem: Int = 0,
    val homeState: UiState<Unit> = UiState.Idle
)
