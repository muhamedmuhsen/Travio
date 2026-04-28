package com.example.domain.usecase.review

import com.example.domain.model.review.Review
import com.example.domain.model.review.ReviewMutationPayload
import com.example.domain.model.review.ReviewsPage
import com.example.domain.repository.review.ReviewRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class UpsertReviewUseCaseTest {

    @Test
    fun `should return review when repository succeeds`() = runTest {
        val expectedReview = review(1)
        val reviewRepository = FakeReviewRepository(submitResult = Result.Success(expectedReview))
        val useCase = UpsertReviewUseCase(reviewRepository)

        val result = useCase(1, 4, "Nice")

        assertEquals(Result.Success<Review, DataError>(expectedReview), result)
    }

    @Test
    fun `should return error when rating is too low`() = runTest {
        val useCase = UpsertReviewUseCase(FakeReviewRepository())

        val result = useCase(1, 0, "Nice")

        assertEquals(Result.Error<Review, DataError>(DataError.Validation.InvalidInputs), result)
    }

    @Test
    fun `should return error when rating is too high`() = runTest {
        val useCase = UpsertReviewUseCase(FakeReviewRepository())

        val result = useCase(1, 6, "Nice")

        assertEquals(Result.Error<Review, DataError>(DataError.Validation.InvalidInputs), result)
    }

    private class FakeReviewRepository(
        private val submitResult: Result<Review, DataError> = Result.Error(DataError.UnknownError)
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
        ): Result<ReviewMutationPayload, DataError> {
            return when (val current = submitResult) {
                is Result.Success -> Result.Success(ReviewMutationPayload(review = current.data, aggregate = null))
                is Result.Error -> Result.Error(current.error)
            }
        }

        override suspend fun deleteReview(
            destinationId: Int,
            reviewId: Int
        ): Result<Unit, DataError> = Result.Error(DataError.UnknownError)
    }

    private companion object {
        fun review(id: Int) = Review(
            id = id,
            authorName = "Author",
            authorAvatarUrl = null,
            rating = 4,
            content = "Content",
            createdAt = Instant.now(),
            helpfulCount = 0
        )
    }
}
