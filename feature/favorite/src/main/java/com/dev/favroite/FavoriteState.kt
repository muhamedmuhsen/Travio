package com.dev.favroite

import ui.state.UiState

data class FavoriteState(
    val selectedItem: Int = 0,
    val favoriteState: UiState<Unit> = UiState.Idle
)
