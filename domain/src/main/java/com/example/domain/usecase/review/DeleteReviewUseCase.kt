package com.example.domain.usecase.review

import com.example.domain.repository.destinations.DestinationsRepository
import com.example.domain.repository.review.ReviewRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import javax.inject.Inject

class DeleteReviewUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val destinationsRepository: DestinationsRepository
) {
    suspend operator fun invoke(
        destinationId: Int,
        reviewId: Int
    ): Result<Unit, DataError> {
        return when (val destinationResult = destinationsRepository.getDestinationsById(destinationId)) {
            is Result.Success -> reviewRepository.deleteReview(destinationId, reviewId)
            is Result.Error -> Result.Error(destinationResult.error)
        }
    }
}
