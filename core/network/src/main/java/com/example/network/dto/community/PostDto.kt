package com.example.network.dto.community

import com.google.gson.annotations.SerializedName

data class PostDto(
    @SerializedName("id") val postId: Int = 0,
    @SerializedName("authorId") val authorId: Int = 0,
    @SerializedName("authorName") val authorName: String? = null,
    @SerializedName("authorAvatarUrl") val authorAvatarUrl: String? = null,
    @SerializedName("location") val location: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("content") val content: String? = null,
    @SerializedName("postImageUrls") val imageUrls: List<String>? = null,
    @SerializedName("likesCount") val likesCount: Int = 0,
    @SerializedName("commentsCount") val commentsCount: Int = 0,
    @SerializedName("rating") val rating: Float = 0f,
    @SerializedName("isLikedByCurrentUser") val isLiked: Boolean = false,
    @SerializedName("comments") val comments: List<CommentDto>? = null
)
