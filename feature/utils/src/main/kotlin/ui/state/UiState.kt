package ui.state

sealed interface UiState<out T> {
    data object Idle : UiState<Nothing>
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T? = null) : UiState<T>
    data class Error(val message: UiText) : UiState<Nothing>
}