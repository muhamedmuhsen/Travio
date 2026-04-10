package com.dev.destination.presentation

import com.example.domain.model.destination.Destination

sealed interface UiState<out T> {
    data object Idle : UiState<Nothing>
    data object Loading : UiState<Nothing>
    data class Success<out T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

data class DestinationDetailUiState(
    val detailState: UiState<Destination> = UiState.Idle,
    val relatedDestinationsState: UiState<List<Destination>> = UiState.Idle,
    val isFavorite: Boolean = false
)
