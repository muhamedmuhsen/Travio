package com.dev.search.presentation.flights

import com.dev.utils.uitext.UiText

sealed interface FlightSearchEvent {
    data class ShowSnackbar(val message: UiText) : FlightSearchEvent
    data object NavigateBack : FlightSearchEvent
    data class NavigateToFlightDetails(val offerId: String, val passengerIds: List<String>) : FlightSearchEvent
}
