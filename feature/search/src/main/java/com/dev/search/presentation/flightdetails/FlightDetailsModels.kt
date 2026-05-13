package com.dev.search.presentation.flightdetails

import com.dev.utils.uitext.UiText

data class FlightDetailsUiModel(
    val summary: FlightDetailsSummaryUi,
    val price: PriceSectionUi,
    val flightInfo: FlightInfoUi,
    val timeline: List<TimelineItemUi>,
    val extras: ExtrasUi
)

data class FlightDetailsSummaryUi(
    val offerId: String,
    val airlineName: String,
    val flightNumber: String,
    val departureTime: String,
    val departureDate: String,
    val departureDateFull: String,
    val arrivalTime: String,
    val arrivalDate: String,
    val arrivalDateFull: String,
    val origin: String,
    val originCity: String?,
    val destination: String,
    val destinationCity: String?,
    val stopsLabel: UiText,
    val totalDuration: String
)

data class PriceSectionUi(
    val basePrice: Double,
    val taxes: Double,
    val totalPrice: Double,
    val currency: String,
    val pricePerPerson: Double?
)

data class FlightInfoUi(
    val flightNumber: String,
    val aircraftName: String,
    val cabinClass: String?,
    val stopsLabel: UiText,
    val airlineName: String
)

sealed interface TimelineItemUi {
    data class Departure(
        val airport: String,
        val time: String,
        val date: String
    ) : TimelineItemUi

    data class Flight(
        val airlineAndFlightNumber: String,
        val duration: String
    ) : TimelineItemUi

    data class Layover(
        val duration: String,
        val location: String
    ) : TimelineItemUi

    data class Arrival(
        val airport: String,
        val time: String,
        val date: String
    ) : TimelineItemUi
}

data class ExtrasUi(
    val checkedBags: Int,
    val baggageNote: String,
    val refundable: Boolean,
    val penaltyAmount: Double?
)
