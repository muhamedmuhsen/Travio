package com.dev.destination.presentation

import androidx.lifecycle.SavedStateHandle
import com.example.domain.model.auth.User
import com.example.domain.model.review.Review
import com.example.domain.model.review.ReviewAggregate
import com.example.domain.model.review.ReviewMutationPayload
import com.example.domain.model.review.ReviewsPage
import com.example.domain.repository.review.ReviewRepository
import com.example.domain.repository.usermanagement.UserManagementRepository
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
    private lateinit var fakeUserManagementRepository: FakeUserManagementRepository
    private lateinit var getReviewsPageUseCase: GetReviewsPageUseCase
    private lateinit var upsertReviewUseCase: UpsertReviewUseCase
    private lateinit var deleteReviewUseCase: DeleteReviewUseCase

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeReviewRepository()
        fakeUserManagementRepository = FakeUserManagementRepository()
        getReviewsPageUseCase = GetReviewsPageUseCase(fakeRepository)
        upsertReviewUseCase = UpsertReviewUseCase(fakeRepository)
        deleteReviewUseCase = DeleteReviewUseCase(fakeRepository)
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
            userManagementRepository = fakeUserManagementRepository,
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
    fun `given_initialLoadFails_when_retried_then_errorCleared`() = runTest {
        fakeRepository.pageResult = Result.Error(DataError.Network.ServerError)
        val viewModel = createViewModel()
        advanceUntilIdle()
        assertTrue(viewModel.state.value.initialError != null)

        fakeRepository.pageResult = Result.Success(ReviewsPage(1, 10, 0, emptyList()))
        viewModel.loadInitialReviews()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertNull(state.initialError)
        assertFalse(state.initialLoading)
    }

    @Test
    fun `given_emptyReviewList_when_loaded_then_stableEmptyState`() = runTest {
        fakeRepository.pageResult = Result.Success(ReviewsPage(1, 10, 0, emptyList()))
        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.reviews.isEmpty())
        assertNull(state.initialError)
        assertFalse(state.initialLoading)
        assertEquals(0, state.summary?.totalReviews)
    }

    @Test
    fun `given_ratingOutOfRange_when_upsertCalled_then_validationErrorNoNetworkCall`() = runTest {
        fakeRepository.pageResult = Result.Success(ReviewsPage(1, 10, 0, emptyList()))
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.upsertReview(0, "Bad")
        advanceUntilIdle()
        assertTrue(viewModel.state.value.submitError != null)
        assertTrue(viewModel.state.value.reviews.isEmpty())

        viewModel.upsertReview(6, "Bad")
        advanceUntilIdle()
        assertTrue(viewModel.state.value.submitError != null)
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
    fun `given_userReviewOnPage2_when_loadMoreCalled_then_currentUserReviewIdentified`() = runTest {
        fakeRepository.pageResult = Result.Success(ReviewsPage(1, 10, 2, listOf(review(1, isOwned = false))))
        val viewModel = createViewModel()
        advanceUntilIdle()
        assertNull(viewModel.state.value.currentUserReview)

        fakeRepository.pageResult = Result.Success(ReviewsPage(2, 10, 2, listOf(review(2, isOwned = true))))
        viewModel.loadMoreReviews()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue("currentUserReview should be identified from page 2", state.currentUserReview != null)
        assertEquals(2, state.currentUserReview?.id)
    }

    @Test
    fun `given_userReviewOnPage2_when_upsertCalled_then_noListDuplicate`() = runTest {
        fakeRepository.pageResult = Result.Success(ReviewsPage(1, 10, 2, listOf(review(1, isOwned = false))))
        val viewModel = createViewModel()
        advanceUntilIdle()

        fakeRepository.pageResult = Result.Success(ReviewsPage(2, 10, 2, listOf(review(2, isOwned = true))))
        viewModel.loadMoreReviews()
        advanceUntilIdle()

        val updatedReview = review(2, isOwned = true).copy(content = "Updated")
        fakeRepository.submitResult = Result.Success(updatedReview)

        viewModel.upsertReview(4, "Updated")
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals("Should only have 2 reviews (no duplicate)", 2, state.reviews.size)
        assertEquals(1, state.reviews.count { it.isOwnedByCurrentUser })
    }

    @Test
    fun `given_existingUserReview_when_upsertSucceeds_then_authorNamePreserved`() = runTest {
        val existingReview = review(1, isOwned = true).copy(authorName = "Alice")
        fakeRepository.pageResult = Result.Success(ReviewsPage(1, 10, 1, listOf(existingReview)))
        val viewModel = createViewModel()
        advanceUntilIdle()

        val serverResponseReview = review(1, isOwned = true).copy(authorName = "Generic")
        fakeRepository.submitResult = Result.Success(serverResponseReview)

        viewModel.upsertReview(5, "Great")
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals("Alice", state.currentUserReview?.authorName)
        assertEquals("Alice", state.reviews.first { it.id == 1 }.authorName)
    }

    @Test
    fun `given_noUserReview_when_upsertSucceeds_then_authorNameFetchedFromProfile`() = runTest {
        fakeRepository.pageResult = Result.Success(ReviewsPage(1, 10, 0, emptyList()))
        fakeUserManagementRepository.userResult = Result.Success(
            User(firstName = "John", lastName = "Doe", username = "jdoe", email = "j@d.com", profilePictureUrl = "url")
        )
        val viewModel = createViewModel()
        advanceUntilIdle()

        val serverResponseReview = review(1, isOwned = true).copy(authorName = "")
        fakeRepository.submitResult = Result.Success(serverResponseReview)

        viewModel.upsertReview(5, "Great")
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals("John Doe", state.currentUserReview?.authorName)
        assertEquals("url", state.currentUserReview?.authorAvatarUrl)
    }

    @Test
    fun `given_reviewsOnPage1Only_when_reviewDeleted_then_averageRatingNotRecalculatedFromPartialPage`() = runTest {
        val myReview = review(1, isOwned = true, rating = 5)
        fakeRepository.pageResult = Result.Success(ReviewsPage(1, 10, 5, listOf(myReview)))
        val viewModel = createViewModel()
        advanceUntilIdle()

        val priorAverage = viewModel.state.value.summary?.averageRating

        fakeRepository.deleteResult = Result.Success(Unit)
        viewModel.deleteReview()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(priorAverage, state.summary?.averageRating)
        assertEquals(4, state.summary?.totalReviews)
    }

    @Test
    fun `given_allReviewsLoaded_when_reviewDeleted_then_averageRatingRecalculatedCorrectly`() = runTest {
        val myReview = review(1, isOwned = true, rating = 5)
        val otherReview = review(2, isOwned = false, rating = 3)
        fakeRepository.pageResult = Result.Success(ReviewsPage(1, 10, 2, listOf(myReview, otherReview)))
        val viewModel = createViewModel()
        advanceUntilIdle()

        fakeRepository.deleteResult = Result.Success(Unit)
        viewModel.deleteReview()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(3, state.summary?.averageRating)
        assertEquals(1, state.summary?.totalReviews)
        assertFalse(state.isSummarySyncedFromServer)
    }

    @Test
    fun `given_upsertReturnsAggregate_when_averageIs4dot9_then_summaryRoundsTo5`() = runTest {
        fakeRepository.pageResult = Result.Success(ReviewsPage(1, 10, 2, listOf(review(2))))
        val viewModel = createViewModel()
        advanceUntilIdle()

        val myReview = review(1, isOwned = true, rating = 5)
        fakeRepository.submitResult = Result.Success(myReview)
        fakeRepository.submitAggregate = ReviewAggregate(averageRating = 4.9, totalReviews = 3)

        viewModel.upsertReview(5, "Amazing")
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(5, state.summary?.averageRating)
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
        var submitAggregate: ReviewAggregate? = null
        var deleteResult: Result<Unit, DataError> = Result.Error(DataError.UnknownError)

        override suspend fun getReviewsByDestinationId(
            destinationId: Int,
            pageIndex: Int,
            pageSize: Int
        ): Result<ReviewsPage, DataError> = pageResult

        override suspend fun submitReviewWithAggregate(
            destinationId: Int,
            rating: Int,
            content: String
        ): Result<ReviewMutationPayload, DataError> {
            return when (val current = submitResult) {
                is Result.Success -> Result.Success(ReviewMutationPayload(review = current.data, aggregate = submitAggregate))
                is Result.Error -> Result.Error(current.error)
            }
        }

        override suspend fun deleteReview(
            destinationId: Int,
            reviewId: Int
        ): Result<Unit, DataError> = deleteResult
    }

    private class FakeUserManagementRepository : UserManagementRepository {
        var userResult: Result<User, DataError> = Result.Error(DataError.UnknownError)
        override suspend fun getUser(): Result<User, DataError> = userResult
        override suspend fun updateProfile(firstName: String, lastName: String, username: String): Result<User, DataError> = Result.Error(DataError.UnknownError)
        override suspend fun updateProfilePic(imageUri: String): Result<String, DataError> = Result.Error(DataError.UnknownError)
    }

    private companion object {
        fun review(id: Int, isOwned: Boolean = false, rating: Int = 5) = Review(
            id = id,
            authorName = "Author",
            authorAvatarUrl = null,
            rating = rating,
            content = "Content",
            createdAt = Instant.now(),
            helpfulCount = 0,
            isOwnedByCurrentUser = isOwned
        )
    }
}
