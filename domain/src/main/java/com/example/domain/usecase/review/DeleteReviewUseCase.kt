package com.example.domain.usecase.review

import com.example.domain.repository.review.ReviewRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import javax.inject.Inject

class DeleteReviewUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository
) {
    suspend operator fun invoke(
        destinationId: Int,
        reviewId: Int
    ): Result<Unit, DataError> {
        return reviewRepository.deleteReview(destinationId, reviewId)
    }
}
