package com.example.common.uistateholder

sealed interface UiState<out T> {
    data object Idle : UiState<Nothing>
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T? = null) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}