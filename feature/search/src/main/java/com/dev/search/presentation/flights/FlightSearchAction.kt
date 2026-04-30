package com.dev.search.presentation.flights

sealed interface FlightSearchAction {
    data class OnOriginChanged(val origin: String) : FlightSearchAction
    data class OnDestinationChanged(val destination: String) : FlightSearchAction
    data class OnDepartureDateChanged(val date: String) : FlightSearchAction
    data class OnAdultsChanged(val adults: Int) : FlightSearchAction
    data class OnCabinClassChanged(val cabinClass: String) : FlightSearchAction
    data class OnMaxStopsChanged(val maxStops: Int?) : FlightSearchAction

    data object OnSearchClicked : FlightSearchAction
    data object OnRetrySearch : FlightSearchAction
    data object OnBackClicked : FlightSearchAction
}
