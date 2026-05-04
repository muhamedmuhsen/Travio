package com.example.domain.model.flights.details

data class Policies(
    val refundable: Boolean,
    val penaltyAmount: Double?,
    val checkedBags: Int,
    val baggageNote: String
)
