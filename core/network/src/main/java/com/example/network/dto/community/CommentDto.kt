package com.example.network.dto.community

import com.google.gson.annotations.SerializedName

data class CommentDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("authorName") val authorName: String? = null,
    @SerializedName("authorProfilePictureUrl") val authorAvatarUrl: String? = null,
    @SerializedName("creationDate") val createdAt: String? = null,
    @SerializedName("content") val content: String?
)
