package com.example.domain.model.flights.details

data class FlightDetails(
    val offerId: String,
    val totalPrice: Double,
    val taxAmount: Double,
    val currency: String,
    val totalDuration: String,
    val checkedBags: Int,
    val isRefundable: Boolean,
    val refundPenaltyAmount: Double?,
    val pricePerPerson: Double?,
    val passengerIds: List<String>,
    val segments: List<FlightDetailsSegment>,
    val stops: Int,
    val originAirport: String,
    val originCity: String?,
    val destinationAirport: String,
    val destinationCity: String?,
    val departureTime: String,
    val arrivalTime: String,
    val layovers: List<Layover>,
    val priceBreakdown: PriceBreakdown,
    val policies: Policies,
    val isTimeDataValid: Boolean
)

data class Layover(
    val duration: String,
    val location: String
)
