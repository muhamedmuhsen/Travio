package com.example.network.dto.community

import com.google.gson.annotations.SerializedName

data class CommentContentRequest(
    @SerializedName("content") val content: String,
    @SerializedName("postId") val postId: Int
)
