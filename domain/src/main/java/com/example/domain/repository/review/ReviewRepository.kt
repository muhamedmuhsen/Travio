package com.example.domain.repository.review

import com.example.domain.model.review.ReviewMutationPayload
import com.example.domain.model.review.ReviewsPage
import com.example.domain.utils.DataError
import com.example.domain.utils.Result

interface ReviewRepository {
    suspend fun getReviewsByDestinationId(
        destinationId: Int,
        pageIndex: Int = 1,
        pageSize: Int = 10
    ): Result<ReviewsPage, DataError>

    suspend fun submitReviewWithAggregate(
        destinationId: Int,
        rating: Int,
        content: String
    ): Result<ReviewMutationPayload, DataError>

    suspend fun deleteReview(
        destinationId: Int,
        reviewId: Int
    ): Result<Unit, DataError>

    suspend fun deleteReviewWithAggregate(
        destinationId: Int,
        reviewId: Int
    ): Result<ReviewMutationPayload, DataError> {
        return when (val result = deleteReview(destinationId, reviewId)) {
            is Result.Success -> Result.Success(ReviewMutationPayload(review = null, aggregate = null))
            is Result.Error -> Result.Error(result.error)
        }
    }
}
