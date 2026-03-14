package com.example.network.dto.community

import com.google.gson.annotations.SerializedName

data class PostCreationResponse(
    @SerializedName("id") val postId: Int
)
