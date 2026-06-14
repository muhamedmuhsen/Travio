package com.example.network.dto.flights.booking

import com.google.gson.annotations.SerializedName

data class PaymentIntentRequestDto(
    @SerializedName("offerId") val offerId: String,
    @SerializedName("passengers") val passengers: List<PassengerDto>
)

data class PassengerDto(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("givenName") val givenName: String,
    @SerializedName("familyName") val familyName: String,
    @SerializedName("bornOn") val bornOn: String,
    @SerializedName("email") val email: String,
    @SerializedName("phoneNumber") val phoneNumber: String,
    @SerializedName("gender") val gender: String
)
