package com.example.domain.usecase.review

import com.example.domain.model.review.Review
import com.example.domain.model.review.ReviewMutationPayload
import com.example.domain.repository.destinations.DestinationsRepository
import com.example.domain.repository.review.ReviewRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import javax.inject.Inject

class UpsertReviewUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val destinationsRepository: DestinationsRepository
) {
    suspend operator fun invoke(
        destinationId: Int,
        rating: Int,
        content: String
    ): Result<Review, DataError> {
        if (rating !in 1..5) {
            return Result.Error(DataError.Validation.InvalidInputs)
        }
        return when (val destinationResult = destinationsRepository.getDestinationsById(destinationId)) {
            is Result.Success -> reviewRepository.submitReview(destinationId, rating, content)
            is Result.Error -> Result.Error(destinationResult.error)
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
        return when (val destinationResult = destinationsRepository.getDestinationsById(destinationId)) {
            is Result.Success -> reviewRepository.submitReviewWithAggregate(destinationId, rating, content)
            is Result.Error -> Result.Error(destinationResult.error)
        }
    }
}
