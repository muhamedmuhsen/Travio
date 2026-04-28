package com.example.domain.usecase.review

import com.example.domain.model.review.Review
import com.example.domain.model.review.ReviewMutationPayload
import com.example.domain.repository.review.ReviewRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import javax.inject.Inject

class UpsertReviewUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository
) {
    suspend operator fun invoke(
        destinationId: Int,
        rating: Int,
        content: String
    ): Result<Review, DataError> {
        return when (val result = invokeWithAggregate(destinationId, rating, content)) {
            is Result.Success -> {
                val review = result.data.review
                if (review != null) {
                    Result.Success(review)
                } else {
                    Result.Error(DataError.UnknownError)
                }
            }
            is Result.Error -> Result.Error(result.error)
        }
    }

    suspend fun invokeWithAggregate(
        destinationId: Int,
        rating: Int,
        content: String
    ): Result<ReviewMutationPayload, DataError> {
        if (rating !in 1..5) {
            return Result.Error(DataError.Validation.InvalidInputs)
        }
        return reviewRepository.submitReviewWithAggregate(destinationId, rating, content)
    }
}
