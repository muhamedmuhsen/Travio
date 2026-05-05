package com.example.network.dto.flights.booking

import com.google.gson.annotations.SerializedName

data class FlightOrderRequestDto(
    @SerializedName("offer_id") val offerId: String,
    @SerializedName("passengers") val passengers: List<PassengerDto>
)

data class PassengerDto(
    @SerializedName("title") val title: String,
    @SerializedName("given_name") val givenName: String,
    @SerializedName("family_name") val familyName: String,
    @SerializedName("born_on") val bornOn: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone_number") val phoneNumber: String,
    @SerializedName("gender") val gender: String
)
