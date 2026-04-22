package com.example.data.repository.review

import com.example.data.mapper.review.toReview
import com.example.data.utils.safeApiCall
import com.example.domain.model.review.Review
import com.example.domain.repository.review.ReviewRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import com.example.network.api.ReviewsApi
import com.example.network.dto.review.SubmitReviewRequest
import javax.inject.Inject

class ReviewRepositoryImpl @Inject constructor(
    private val api: ReviewsApi
) : ReviewRepository {
    override suspend fun getReviewsByDestinationId(destinationId: Int): Result<List<Review>, DataError> =
        safeApiCall {
            api.getReviewsByDestinationId(destinationId).data.map { it.toReview() }
        }

    override suspend fun submitReview(
        destinationId: Int,
        rating: Float,
        content: String
    ): Result<Unit, DataError> =
        safeApiCall {
            api.submitReview(
                SubmitReviewRequest(
                    destinationId = destinationId,
                    rating = rating,
                    content = content
                )
            )
            Unit
        }
}
