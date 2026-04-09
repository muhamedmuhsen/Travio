package com.example.network.dto.community

import com.google.gson.annotations.SerializedName

data class PostWithCommentsDto(
    @SerializedName("id") val postId: Int,
    @SerializedName("authorId") val authorId: String?,
    @SerializedName("authorName") val authorName: String?,
    @SerializedName("authorProfilePictureUrl") val authorAvatarUrl: String?,
    @SerializedName("location") val location: String?,
    @SerializedName("creationDate") val createdAt: String?,
    @SerializedName("content") val content: String?,
    @SerializedName("imageUrls") val imageUrls: List<String>?,
    @SerializedName("likesCount") val likesCount: Int,
    @SerializedName("commentsCount") val commentsCount: Int,
    @SerializedName("isLikedByCurrentUser") val isLiked: Boolean,
    @SerializedName("comments") val commentDto: List<CommentDto>
)
