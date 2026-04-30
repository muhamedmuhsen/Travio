package com.example.network.dto.flights

import com.google.gson.annotations.SerializedName

data class TopOffersResponseDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: List<TopOfferDto>?
)
