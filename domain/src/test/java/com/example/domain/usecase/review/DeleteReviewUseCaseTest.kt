package com.example.domain.usecase.review

import com.example.domain.model.review.Review
import com.example.domain.model.review.ReviewsPage
import com.example.domain.repository.review.ReviewRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class DeleteReviewUseCaseTest {

    @Test
    fun `should return success when repository succeeds`() = runTest {
        val reviewRepository = FakeReviewRepository(deleteResult = Result.Success(Unit))
        val useCase = DeleteReviewUseCase(reviewRepository)

        val result = useCase(1, 1)

        assertEquals(Result.Success<Unit, DataError>(Unit), result)
    }

    private class FakeReviewRepository(
        private val deleteResult: Result<Unit, DataError> = Result.Error(DataError.UnknownError)
    ) : ReviewRepository {
        override suspend fun getReviewsByDestinationId(
            destinationId: Int,
            pageIndex: Int,
            pageSize: Int
        ): Result<ReviewsPage, DataError> = Result.Error(DataError.UnknownError)

        override suspend fun submitReviewWithAggregate(
            destinationId: Int,
            rating: Int,
            content: String
        ): Result<com.example.domain.model.review.ReviewMutationPayload, DataError> = Result.Error(DataError.UnknownError)

        override suspend fun deleteReview(
            destinationId: Int,
            reviewId: Int
        ): Result<Unit, DataError> = deleteResult
    }
}
