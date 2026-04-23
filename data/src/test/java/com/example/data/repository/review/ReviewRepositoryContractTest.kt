package com.example.data.repository.review

import com.example.data.mapper.review.toReviewMutationPayload
import com.example.network.dto.review.ReviewSubmitResponseDto
import com.example.network.dto.review.ReviewSubmitResponseWrapper
import org.junit.Assert.assertEquals
import org.junit.Test

class ReviewRepositoryContractTest {

    @Test
    fun givenSubmitResponse_whenMapped_thenAggregateAndReviewAreAvailable() {
        val wrapper =
            ReviewSubmitResponseWrapper(
                pageIndex = 1,
                pageSize = 10,
                count = 1,
                data =
                    ReviewSubmitResponseDto(
                        reviewId = 42,
                        destinationId = 7,
                        rating = 5,
                        comment = "Great place",
                        updatedAtUtc = "2026-04-23T10:15:30Z",
                        averageRating = 4,
                        totalReviews = 120
                    )
            )

        val payload = wrapper.toReviewMutationPayload()

        assertEquals(42, payload.review.id)
        assertEquals(4.0, payload.aggregate?.averageRating ?: 0.0, 0.0)
        assertEquals(120, payload.aggregate?.totalReviews ?: 0)
    }
}

