package com.example.network.dto.usermanagment

import com.google.gson.annotations.SerializedName

data class UpdateProfileRequest(
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("userName") val username: String
)
