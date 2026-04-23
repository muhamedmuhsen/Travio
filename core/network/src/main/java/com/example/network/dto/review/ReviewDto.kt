package com.example.network.dto.review

import com.google.gson.annotations.SerializedName

data class ReviewDto(
    @SerializedName("reviewId") val id: Int,
    @SerializedName("reviewerName") val authorName: String?,
    @SerializedName("reviewerImageUrl") val authorAvatarUrl: String?,
    @SerializedName("rating") val rating: Int,
    @SerializedName("comment") val content: String?,
    @SerializedName("reviewDateUtc") val createdAt: String?,
    @SerializedName("helpfulVotes") val helpfulCount: Int,
    @SerializedName("isMine") val isOwnedByCurrentUser: Boolean
)

data class ReviewSubmitResponseDto(
    val reviewId: Int,
    val destinationId: Int,
    val rating: Int,
    val comment: String,
    val updatedAtUtc: String?,
    val averageRating: Int,
    val totalReviews: Int
)

data class ReviewSubmitResponseWrapper(
    @SerializedName("pageIndex") val pageIndex: Int = 1,
    @SerializedName("pageSize") val pageSize: Int = 10,
    @SerializedName("count") val count: Int = 1,
    @SerializedName("data") val data: ReviewSubmitResponseDto
)

data class ReviewResponseDto(
    val reviewId: Int,
    val reviewerName: String?,
    val reviewerImageUrl: String?,
    val rating: Int,
    val comment: String?,
    val reviewDateUtc: String?,
    val helpfulVotes: Int,
    val isMine: Boolean
)

data class ReviewsResponseDto(
    @SerializedName("count") val count: Int,
    @SerializedName("pageIndex") val pageIndex: Int,
    @SerializedName("pageSize") val pageSize: Int,
    @SerializedName("data") val data: List<ReviewDto>
)

data class SubmitReviewRequest(
    @SerializedName("rating") val rating: Int,
    @SerializedName("comment") val comment: String
)
