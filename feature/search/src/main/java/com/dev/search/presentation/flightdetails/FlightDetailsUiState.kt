package com.dev.search.presentation.flightdetails

import com.dev.utils.uitext.UiText

sealed interface FlightDetailsUiState {
    object Loading : FlightDetailsUiState
    data class Success(
        val data: FlightDetailsUiModel,
        val warning: UiText? = null
    ) : FlightDetailsUiState

    data class Error(
        val message: UiText,
        val isRetryable: Boolean
    ) : FlightDetailsUiState
}
