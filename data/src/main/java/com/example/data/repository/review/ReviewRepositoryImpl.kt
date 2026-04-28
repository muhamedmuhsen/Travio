package com.example.data.repository.review

import com.example.data.mapper.review.toReviewMutationPayload
import com.example.data.mapper.review.toReviewsPage
import com.example.data.utils.safeApiCall
import com.example.domain.model.review.ReviewMutationPayload
import com.example.domain.model.review.ReviewsPage
import com.example.domain.repository.review.ReviewRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import com.example.network.api.ReviewsApi
import com.example.network.dto.review.SubmitReviewRequest
import javax.inject.Inject

class ReviewRepositoryImpl @Inject constructor(
    private val api: ReviewsApi
) : ReviewRepository {
    override suspend fun getReviewsByDestinationId(
        destinationId: Int,
        pageIndex: Int,
        pageSize: Int
    ): Result<ReviewsPage, DataError> =
        safeApiCall {
            api.getReviewsByDestinationId(
                destinationId = destinationId,
                pageIndex = pageIndex,
                pageSize = pageSize
            ).toReviewsPage()
        }

    override suspend fun submitReviewWithAggregate(
        destinationId: Int,
        rating: Int,
        content: String
    ): Result<ReviewMutationPayload, DataError> =
        safeApiCall {
            val response = api.submitReview(
                destinationId = destinationId,
                request = SubmitReviewRequest(
                    rating = rating,
                    comment = content
                )
            )
            response.toReviewMutationPayload()
        }

    override suspend fun deleteReview(
        destinationId: Int,
        reviewId: Int
    ): Result<Unit, DataError> =
        safeApiCall {
            api.deleteReview(destinationId = destinationId)
            Unit
        }
}
