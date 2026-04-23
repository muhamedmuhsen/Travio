package com.example.domain.usecase.review

import com.example.domain.model.destination.Destination
import com.example.domain.model.destination.DestinationsPage
import com.example.domain.model.review.Review
import com.example.domain.model.review.ReviewsPage
import com.example.domain.repository.destinations.DestinationsRepository
import com.example.domain.repository.review.ReviewRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetReviewsPageUseCaseTest {

    @Test
    fun `should return reviews page when repository succeeds`() = runTest {
        val expectedPage = ReviewsPage(1, 10, 1, emptyList())
        val reviewRepository = FakeReviewRepository(pageResult = Result.Success(expectedPage))
        val destinationsRepository = FakeDestinationsRepository()
        val useCase = GetReviewsPageUseCase(reviewRepository, destinationsRepository)

        val result = useCase(1, 1, 10)

        assertEquals(Result.Success<ReviewsPage, DataError>(expectedPage), result)
    }

    @Test
    fun `should return error when pageIndex is invalid`() = runTest {
        val useCase = GetReviewsPageUseCase(FakeReviewRepository(), FakeDestinationsRepository())

        val result = useCase(1, 0, 10)

        assertEquals(Result.Error<ReviewsPage, DataError>(DataError.Validation.InvalidInputs), result)
    }

    @Test
    fun `should return error when pageSize is invalid`() = runTest {
        val useCase = GetReviewsPageUseCase(FakeReviewRepository(), FakeDestinationsRepository())

        val result = useCase(1, 1, 0)

        assertEquals(Result.Error<ReviewsPage, DataError>(DataError.Validation.InvalidInputs), result)
    }

    @Test
    fun `should return error when destination not found`() = runTest {
        val reviewRepository = FakeReviewRepository()
        val destinationsRepository = FakeDestinationsRepository(notFound = true)
        val useCase = GetReviewsPageUseCase(reviewRepository, destinationsRepository)

        val result = useCase(1, 1, 10)

        assertTrue(result is Result.Error)
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

        override suspend fun submitReview(
            destinationId: Int,
            rating: Int,
            content: String
        ): Result<Review, DataError> = Result.Error(DataError.UnknownError)

        override suspend fun deleteReview(
            destinationId: Int,
            reviewId: Int
        ): Result<Unit, DataError> = Result.Error(DataError.UnknownError)
    }

    private class FakeDestinationsRepository(
        private val notFound: Boolean = false
    ) : DestinationsRepository {
        override suspend fun getDestinationsById(destinationId: Int): Result<Destination, DataError> =
            if (notFound) Result.Error(DataError.Data.NotFound)
            else Result.Success(
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
        ): Result<DestinationsPage, DataError> = Result.Success(DestinationsPage(1, 10, 0, emptyList()))

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
}
