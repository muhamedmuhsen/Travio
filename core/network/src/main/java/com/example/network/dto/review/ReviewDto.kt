package com.example.network.dto.review

import com.google.gson.annotations.SerializedName

data class ReviewDto(
    @SerializedName("id") val id: Int,
    @SerializedName("authorName") val authorName: String?,
    @SerializedName("authorProfilePictureUrl") val authorAvatarUrl: String?,
    @SerializedName("authorLocation") val authorLocation: String?,
    @SerializedName("rating") val rating: Float,
    @SerializedName("title") val title: String?,
    @SerializedName("content") val content: String?,
    @SerializedName("creationDate") val createdAt: String?,
    @SerializedName("helpfulCount") val helpfulCount: Int,
    @SerializedName("isHelpfulByCurrentUser") val isHelpful: Boolean
)

data class SubmitReviewRequest(
    @SerializedName("destinationId") val destinationId: Int,
    @SerializedName("rating") val rating: Float,
    @SerializedName("content") val content: String
)
