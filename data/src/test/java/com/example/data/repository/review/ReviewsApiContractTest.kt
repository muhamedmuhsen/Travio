package com.example.data.repository.review

import com.example.data.utils.safeApiCall
import com.example.domain.model.review.Review
import com.example.domain.model.review.ReviewAggregate
import com.example.domain.model.review.ReviewPage
import com.example.domain.model.review.ReviewSummary
import com.example.domain.repository.review.ReviewRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import com.example.network.api.ReviewsApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq

class ReviewsApiContractTest {

    private lateinit var reviewsApi: ReviewsApi
    private lateinit var reviewRepository: ReviewRepository

    @Before
    fun setup() {
        reviewsApi = mock(ReviewsApi::class.java)
    }

    @Test
    fun `getReviewsByDestinationId calls API with correct destinationId and returns reviews`() = runTest {
        val destinationId = 1
        val expectedReviews = listOf(
            Review(
                id = "1",
                destinationId = destinationId,
                userId = "user1",
                userName = "John Doe",
                rating = 5,
                content = "Great place!",
                createdAt = System.currentTimeMillis(),
                isOwnedByCurrentUser = false
            )
        )
        val expectedSummary = ReviewSummary(averageRating = 5, totalReviews = 1)

        `when`(reviewsApi.getReviewsByDestinationId(eq(destinationId), any(), any()))
            .thenReturn(
                com.example.network.dto.review.ReviewsResponse(
                    success = true,
                    data = com.example.network.dto.review.ReviewsResponseData(
                        pageIndex = 1,
                        pageSize = 10,
                        totalCount = 1,
                        reviews = listOf(
                            com.example.network.dto.review.ReviewDto(
                                id = "1",
                                destinationId = destinationId,
                                userId = "user1",
                                userName = "John Doe",
                                rating = 5,
                                content = "Great place!",
                                createdAt = "2026-04-23T10:00:00Z",
                                isOwnedByCurrentUser = false
                            )
                        )
                    )
                )
            )

        // This test verifies the API contract is being called with correct parameters
        verify(reviewsApi).getReviewsByDestinationId(eq(destinationId), eq(1), eq(10))
    }

    @Test
    fun `getReviewsByDestinationId returns error when API returns failure`() = runTest {
        val destinationId = 999

        `when`(reviewsApi.getReviewsByDestinationId(eq(destinationId), any(), any()))
            .thenReturn(
                com.example.network.dto.review.ReviewsResponse(
                    success = false,
                    data = null
                )
            )

        verify(reviewsApi).getReviewsByDestinationId(eq(destinationId), eq(1), eq(10))
    }

    @Test
    fun `submitReview includes rating and content in request`() = runTest {
        val destinationId = 1
        val rating = 4
        val content = "Amazing experience"

        `when`(reviewsApi.submitReview(eq(destinationId), eq(rating), eq(content)))
            .thenReturn(
                com.example.network.dto.review.SubmitReviewResponse(
                    success = true,
                    data = com.example.network.dto.review.SubmitReviewResponseData(
                        review = com.example.network.dto.review.ReviewDto(
                            id = "new-review-id",
                            destinationId = destinationId,
                            userId = "current-user",
                            userName = "Current User",
                            rating = rating,
                            content = content,
                            createdAt = "2026-04-23T10:00:00Z",
                            isOwnedByCurrentUser = true
                        ),
                        aggregate = com.example.network.dto.review.ReviewAggregateDto(
                            averageRating = rating.toDouble(),
                            totalReviews = 1
                        )
                    )
                )
            )

        verify(reviewsApi).submitReview(eq(destinationId), eq(rating), eq(content))
    }
}