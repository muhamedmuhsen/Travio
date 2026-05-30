package com.example.network.dto.hotel.booking

import com.google.gson.annotations.SerializedName

data class CancelBookingResponseDto(
    @SerializedName("data") val data: CancelBookingDataDto?,
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("errors") val errors: List<String>?
)

data class CancelBookingDataDto(
    @SerializedName("reference") val reference: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("cancellationReference") val cancellationReference: String?
)
