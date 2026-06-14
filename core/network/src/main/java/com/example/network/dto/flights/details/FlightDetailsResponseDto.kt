package com.example.network.dto.flights.details

import com.google.gson.annotations.SerializedName

data class FlightDetailsResponseDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("errors") val errors: List<String>?,
    @SerializedName("data") val data: FlightDetailsDto?
)

data class FlightDetailsDto(
    @SerializedName("offerId") val offerId: String?,
    @SerializedName("totalPrice") val totalPrice: Double?,
    @SerializedName("taxAmount") val taxAmount: Double?,
    @SerializedName("currency") val currency: String?,
    @SerializedName("totalDuration") val totalDuration: String?,
    @SerializedName("checkedBags") val checkedBags: Int?,
    @SerializedName("isRefundable") val isRefundable: Boolean?,
    @SerializedName("refundPenaltyAmount") val refundPenaltyAmount: Double?,
    @SerializedName("pricePerPerson") val pricePerPerson: Double?,
    @SerializedName("passengerIds") val passengerIds: List<String>?,
    @SerializedName("segments") val segments: List<FlightDetailsSegmentDto>?
)
