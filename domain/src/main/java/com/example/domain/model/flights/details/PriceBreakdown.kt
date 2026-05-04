package com.example.domain.model.flights.details

data class PriceBreakdown(
    val basePrice: Double,
    val taxes: Double,
    val totalPrice: Double,
    val pricePerPerson: Double?
)
