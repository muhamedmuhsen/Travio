package com.dev.search.presentation

import com.dev.utils.uitext.UiText
import com.example.domain.model.flights.TopFlightOffer

sealed interface TopOffersUiState {
    data object Idle : TopOffersUiState
    data object Loading : TopOffersUiState
    data class Success(val offers: List<TopFlightOffer>) : TopOffersUiState
    data class Error(val message: UiText) : TopOffersUiState
}
