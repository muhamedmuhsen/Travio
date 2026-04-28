package com.example.domain.usecase.review

import com.example.domain.model.review.ReviewsPage
import com.example.domain.repository.review.ReviewRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import javax.inject.Inject

class GetReviewsPageUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository
) {
    suspend operator fun invoke(
        destinationId: Int,
        pageIndex: Int,
        pageSize: Int
    ): Result<ReviewsPage, DataError> {
        if (pageIndex < 1 || pageSize < 1) {
            return Result.Error(DataError.Validation.InvalidInputs)
        }
        return reviewRepository.getReviewsByDestinationId(destinationId, pageIndex, pageSize)
    }
}
