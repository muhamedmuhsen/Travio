package com.example.network.dto.community

import com.google.gson.annotations.SerializedName

data class PostDto(
    @SerializedName("id") val postId: Int,
    @SerializedName("autherId") val authorId: String?,
    @SerializedName("autherName") val authorName: String?,
    @SerializedName("authorProfilePictureUrl") val authorAvatarUrl: String?,
    @SerializedName("location") val location: String?,
    @SerializedName("creationDate") val createdAt: String?,
    @SerializedName("content") val content: String?,
    @SerializedName("postImagesUrls") val imageUrls: List<String>?,
    @SerializedName("likesCount") val likesCount: Int,
    @SerializedName("commentsCount") val commentsCount: Int,
    @SerializedName("isLikedByCurrentUser") val isLiked: Boolean,
    @SerializedName("comments") val commentDto: List<CommentDto>?
)
