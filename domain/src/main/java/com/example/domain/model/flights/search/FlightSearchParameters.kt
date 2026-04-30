package com.example.domain.model.flights.search

data class FlightSearchParameters(
    val origin: String = "",
    val destination: String = "",
    val departureDate: String = "",
    val adults: Int = 1,
    val cabinClass: String = "Economy",
    val maxStops: Int? = null
) {
    companion object {
        fun default() = FlightSearchParameters()
    }
}
