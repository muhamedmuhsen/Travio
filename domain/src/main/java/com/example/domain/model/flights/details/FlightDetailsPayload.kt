package com.example.domain.model.flights.details

data class FlightDetailsPayload(
    val offerId: String,
    val totalPrice: Double,
    val taxAmount: Double,
    val currency: String,
    val totalDuration: String?,
    val checkedBags: Int,
    val isRefundable: Boolean,
    val refundPenaltyAmount: Double?,
    val pricePerPerson: Double?,
    val segments: List<FlightDetailsSegmentPayload>
)
