package com.dev.search.presentation.flights

import com.dev.utils.uitext.UiText
import com.example.domain.model.flights.search.FlightOffer
import com.example.domain.model.flights.search.FlightSearchParameters

data class FlightSearchUiState(
    val searchParams: FlightSearchParameters = FlightSearchParameters.default(),
    val searchStatus: SearchStatus = SearchStatus.Idle,
    val validationErrors: Map<String, UiText> = emptyMap()
)

sealed interface SearchStatus {
    data object Idle : SearchStatus
    data object Loading : SearchStatus
    data class Success(val offers: List<FlightOffer>) : SearchStatus
    data class Error(val message: UiText, val isRetryable: Boolean) : SearchStatus
}
