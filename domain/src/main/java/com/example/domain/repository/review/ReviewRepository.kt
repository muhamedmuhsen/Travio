package com.example.domain.repository.review

import com.example.domain.model.review.Review
import com.example.domain.utils.DataError
import com.example.domain.utils.Result

interface ReviewRepository {
    suspend fun getReviewsByDestinationId(destinationId: Int): Result<List<Review>, DataError>
    suspend fun submitReview(
        destinationId: Int,
        rating: Float,
        content: String
    ): Result<Unit, DataError>
}
