package com.example.network.dto.flights.booking

import com.google.gson.annotations.SerializedName

data class FlightOrderResponseDto(
    @SerializedName("duffel_order_id") val duffelOrderId: String,
    @SerializedName("pnr") val pnr: String,
    @SerializedName("booking_status") val bookingStatus: String
)
