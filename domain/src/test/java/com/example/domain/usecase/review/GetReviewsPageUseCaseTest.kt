package com.example.domain.usecase.review

import com.example.domain.model.review.Review
import com.example.domain.model.review.ReviewsPage
import com.example.domain.repository.review.ReviewRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetReviewsPageUseCaseTest {

    @Test
    fun `should return reviews page when repository succeeds`() = runTest {
        val expectedPage = ReviewsPage(1, 10, 1, emptyList())
        val reviewRepository = FakeReviewRepository(pageResult = Result.Success(expectedPage))
        val useCase = GetReviewsPageUseCase(reviewRepository)

        val result = useCase(1, 1, 10)

        assertEquals(Result.Success<ReviewsPage, DataError>(expectedPage), result)
    }

    @Test
    fun `should return error when pageIndex is invalid`() = runTest {
        val useCase = GetReviewsPageUseCase(FakeReviewRepository())

        val result = useCase(1, 0, 10)

        assertEquals(Result.Error<ReviewsPage, DataError>(DataError.Validation.InvalidInputs), result)
    }

    @Test
    fun `should return error when pageSize is invalid`() = runTest {
        val useCase = GetReviewsPageUseCase(FakeReviewRepository())

        val result = useCase(1, 1, 0)

        assertEquals(Result.Error<ReviewsPage, DataError>(DataError.Validation.InvalidInputs), result)
    }

    private class FakeReviewRepository(
        private val pageResult: Result<ReviewsPage, DataError> = Result.Success(
            ReviewsPage(1, 10, 0, emptyList())
        )
    ) : ReviewRepository {
        override suspend fun getReviewsByDestinationId(
            destinationId: Int,
            pageIndex: Int,
            pageSize: Int
        ): Result<ReviewsPage, DataError> = pageResult

        override suspend fun submitReviewWithAggregate(
            destinationId: Int,
            rating: Int,
            content: String
        ): Result<com.example.domain.model.review.ReviewMutationPayload, DataError> = Result.Error(DataError.UnknownError)

        override suspend fun deleteReview(
            destinationId: Int,
            reviewId: Int
        ): Result<Unit, DataError> = Result.Error(DataError.UnknownError)
    }
}
