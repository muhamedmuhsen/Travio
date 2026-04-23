package com.example.domain.usecase.review

import com.example.domain.model.destination.Destination
import com.example.domain.model.review.Review
import com.example.domain.model.review.ReviewsPage
import com.example.domain.repository.destinations.DestinationsRepository
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
        val destinationsRepository = FakeDestinationsRepository()
        val useCase = UpsertReviewUseCase(reviewRepository, destinationsRepository)

        val result = useCase(1, 4, "Nice")

        assertEquals(Result.Success<Review, DataError>(expectedReview), result)
    }

    @Test
    fun `should return error when rating is too low`() = runTest {
        val useCase = UpsertReviewUseCase(FakeReviewRepository(), FakeDestinationsRepository())

        val result = useCase(1, 0, "Nice")

        assertEquals(Result.Error<Review, DataError>(DataError.Validation.InvalidInputs), result)
    }

    @Test
    fun `should return error when rating is too high`() = runTest {
        val useCase = UpsertReviewUseCase(FakeReviewRepository(), FakeDestinationsRepository())

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

        override suspend fun submitReview(
            destinationId: Int,
            rating: Int,
            content: String
        ): Result<Review, DataError> = submitResult

        override suspend fun deleteReview(
            destinationId: Int,
            reviewId: Int
        ): Result<Unit, DataError> = Result.Error(DataError.UnknownError)
    }

    private class FakeDestinationsRepository : DestinationsRepository {
        override suspend fun getDestinationsById(destinationId: Int): Result<Destination, DataError> =
            Result.Success(
                Destination(
                    cityName = "City",
                    description = "Desc",
                    destinationID = 1,
                    imageUrls = emptyList(),
                    interests = emptyList(),
                    latitude = 0.0,
                    longitude = 0.0,
                    name = "Name",
                    rating = 4.0,
                    totalReviews = 0
                )
            )

        override suspend fun getAllDestinations(
            pageIndex: Int,
            pageSize: Int,
            cityId: Int?,
            interestId: Int?
        ): Result<List<Destination>, DataError> = Result.Success(emptyList())

        override suspend fun getDestinationsPage(
            pageIndex: Int,
            pageSize: Int,
            cityId: Int?,
            interestId: Int?,
            countryId: Int?
        ): Result<com.example.domain.model.destination.DestinationsPage, DataError> = Result.Success(com.example.domain.model.destination.DestinationsPage(1, 10, 0, emptyList()))

        override suspend fun getTopRatedDestinations(): Result<List<Destination>, DataError> = Result.Success(emptyList())

        override suspend fun getNearbyDestinations(
            latitude: Double,
            longitude: Double,
            radiusKm: Double,
            count: Int
        ): Result<List<Destination>, DataError> = Result.Success(emptyList())

        override suspend fun searchForDestinations(
            keyword: String,
            pageIndex: Int,
            pageSize: Int
        ): Result<List<Destination>, DataError> = Result.Success(emptyList())

        override suspend fun getFamousCountries(): Result<List<com.example.domain.model.destination.Country>, DataError> = Result.Success(emptyList())
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
