package com.example.network.dto.favorite

import com.google.gson.annotations.SerializedName

data class FavoritesResponseDto<T>(
    @SerializedName("data") val data: T?,
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("errors") val errors: List<String>
)
