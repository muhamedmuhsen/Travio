package com.example.network.dto.community

import com.google.gson.annotations.SerializedName

data class PostDto(
    @SerializedName("id") val postId: String,
    @SerializedName("autherId") val authorId: Int,
    @SerializedName("autherName") val authorName: String,
    @SerializedName("autherProfilePictureUrl") val authorAvatarUrl: String,
    @SerializedName("location") val location: String,
    @SerializedName("creationDate") val createdAt: String,
    @SerializedName("content") val content: String,
    @SerializedName("postImageUrls") val imageUrls: List<String>,
    @SerializedName("likesCount") val likesCount: Int,
    @SerializedName("commentsCount") val commentsCount: Int,
    @SerializedName("isLikedByCurrentUser") val isLiked: Boolean
)
