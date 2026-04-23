package com.dev.destination.presentation

import androidx.lifecycle.SavedStateHandle
import com.example.domain.model.destination.Destination
import com.example.domain.model.destination.DestinationsPage
import com.example.domain.model.review.Review
import com.example.domain.model.review.ReviewMutationPayload
import com.example.domain.model.review.ReviewsPage
import com.example.domain.repository.destinations.DestinationsRepository
import com.example.domain.repository.review.ReviewRepository
import com.example.domain.usecase.review.DeleteReviewUseCase
import com.example.domain.usecase.review.GetReviewsPageUseCase
import com.example.domain.usecase.review.UpsertReviewUseCase
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class ReviewsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeReviewRepository
    private lateinit var fakeDestinationsRepository: FakeDestinationsRepository
    private lateinit var getReviewsPageUseCase: GetReviewsPageUseCase
    private lateinit var upsertReviewUseCase: UpsertReviewUseCase
    private lateinit var deleteReviewUseCase: DeleteReviewUseCase

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeReviewRepository()
        fakeDestinationsRepository = FakeDestinationsRepository()
        getReviewsPageUseCase = GetReviewsPageUseCase(fakeRepository, fakeDestinationsRepository)
        upsertReviewUseCase = UpsertReviewUseCase(fakeRepository, fakeDestinationsRepository)
        deleteReviewUseCase = DeleteReviewUseCase(fakeRepository, fakeDestinationsRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(destinationId: Int = 1): ReviewsViewModel {
        return ReviewsViewModel(
            getReviewsPageUseCase = getReviewsPageUseCase,
            upsertReviewUseCase = upsertReviewUseCase,
            deleteReviewUseCase = deleteReviewUseCase,
            savedStateHandle = SavedStateHandle(mapOf("destinationId" to destinationId))
        )
    }

    @Test
    fun `should load initial reviews on init`() = runTest {
        val reviews = listOf(review(1), review(2))
        fakeRepository.pageResult = Result.Success(ReviewsPage(1, 10, 2, reviews))

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.initialLoading)
        assertEquals(2, state.reviews.size)
        assertEquals(2, state.totalCount)
    }

    @Test
    fun `should handle error on initial load`() = runTest {
        fakeRepository.pageResult = Result.Error(DataError.Network.ServerError)

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.initialLoading)
        assertTrue(state.reviews.isEmpty())
        assertTrue(state.initialError != null)
    }

    @Test
    fun `should load more reviews when requested`() = runTest {
        val firstPage = listOf(review(1))
        fakeRepository.pageResult = Result.Success(ReviewsPage(1, 10, 2, firstPage))

        val viewModel = createViewModel()
        advanceUntilIdle()

        val secondPage = listOf(review(2))
        fakeRepository.pageResult = Result.Success(ReviewsPage(2, 10, 2, secondPage))

        viewModel.loadMoreReviews()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(2, state.reviews.size)
        assertEquals(2, state.pagination.pageIndex)
        assertFalse(state.pagination.hasMore)
    }

    @Test
    fun `should upsert review and maintain consistency`() = runTest {
        fakeRepository.pageResult = Result.Success(ReviewsPage(1, 10, 0, emptyList()))
        val viewModel = createViewModel()
        advanceUntilIdle()

        val newReview = review(1, isOwned = true)
        fakeRepository.submitResult = Result.Success(newReview)

        viewModel.upsertReview(5, "Great")
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(newReview, state.currentUserReview)
        assertEquals(1, state.reviews.size)
        assertEquals(1, state.totalCount)
        assertEquals(5, state.summary?.averageRating)
    }

    @Test
    fun `should delete review and maintain consistency`() = runTest {
        val myReview = review(1, isOwned = true)
        fakeRepository.pageResult = Result.Success(ReviewsPage(1, 10, 1, listOf(myReview)))
        val viewModel = createViewModel()
        advanceUntilIdle()

        fakeRepository.deleteResult = Result.Success(Unit)

        viewModel.deleteReview()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertNull(state.currentUserReview)
        assertTrue(state.reviews.isEmpty())
        assertEquals(0, state.totalCount)
    }

    private class FakeReviewRepository : ReviewRepository {
        var pageResult: Result<ReviewsPage, DataError> = Result.Success(ReviewsPage(1, 10, 0, emptyList()))
        var submitResult: Result<Review, DataError> = Result.Error(DataError.UnknownError)
        var deleteResult: Result<Unit, DataError> = Result.Error(DataError.UnknownError)

        override suspend fun getReviewsByDestinationId(
            destinationId: Int,
            pageIndex: Int,
            pageSize: Int
        ): Result<ReviewsPage, DataError> = pageResult

        override suspend fun submitReview(
            destinationId: Int,
            rating: Int,
            content: String
        ): Result<Review, DataError> = submitResult

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
        ): Result<Unit, DataError> = deleteResult
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
                    rating = 4.5,
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
        fun review(id: Int, isOwned: Boolean = false) = Review(
            id = id,
            authorName = "Author",
            authorAvatarUrl = null,
            rating = 5,
            content = "Content",
            createdAt = Instant.now(),
            helpfulCount = 0,
            isOwnedByCurrentUser = isOwned
        )
    }
}
