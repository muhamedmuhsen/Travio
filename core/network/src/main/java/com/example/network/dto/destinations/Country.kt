package com.example.network.dto.destinations

import com.google.gson.annotations.SerializedName

data class Country(
    @SerializedName("countryID") val countryID: Int,
    @SerializedName(value = "imageURL", alternate = ["flagURL"]) val imageURL: String? = null,
    @SerializedName("name") val name: String
)
