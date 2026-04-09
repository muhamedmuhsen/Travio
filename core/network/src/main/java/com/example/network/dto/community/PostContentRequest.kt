package com.example.network.dto.community

import com.google.gson.annotations.SerializedName

data class PostContentRequest(
    @SerializedName("content") val content: String,
    @SerializedName("location") val location: String
)
