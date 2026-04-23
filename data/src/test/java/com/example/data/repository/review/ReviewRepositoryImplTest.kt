package com.example.data.repository.review

import com.example.common.baseresponse.BaseResponse
import com.example.domain.utils.Result
import com.example.network.api.ReviewsApi
import com.example.network.dto.review.ReviewSubmitResponseDto
import com.example.network.dto.review.ReviewSubmitResponseWrapper
import com.example.network.dto.review.ReviewsResponseDto
import com.example.network.dto.review.SubmitReviewRequest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReviewRepositoryImplTest {

    @Test
    fun `should return ReviewsPage when API succeeds`() = runTest {
        val expectedDto = ReviewsResponseDto(count = 1, pageIndex = 1, pageSize = 10, data = emptyList())
        val api = FakeReviewsApi(getResponse = BaseResponse(expectedDto, true, "", emptyList()))
        val repository = ReviewRepositoryImpl(api)

        val result = repository.getReviewsByDestinationId(1, 1, 10)

        assertTrue(result is Result.Success)
        assertEquals(1, (result as Result.Success).data.totalCount)
    }

    @Test
    fun `should return Review when submit succeeds`() = runTest {
        val submitDto = ReviewSubmitResponseDto(
            reviewId = 1,
            destinationId = 1,
            rating = 5,
            comment = "Great",
            updatedAtUtc = "2026-04-23T10:15:30Z",
            averageRating = 4,
            totalReviews = 10
        )
        val wrapper = ReviewSubmitResponseWrapper(pageIndex = 1, pageSize = 10, count = 1, data = submitDto)
        val api = FakeReviewsApi(submitResponse = BaseResponse(wrapper, true, "", emptyList()))
        val repository = ReviewRepositoryImpl(api)

        val result = repository.submitReview(1, 5, "Great")

        assertTrue(result is Result.Success)
        assertEquals(1, (result as Result.Success).data.id)
    }

    @Test
    fun `should return success when delete succeeds`() = runTest {
        val api = FakeReviewsApi(deleteResponse = BaseResponse(Unit, true, "", emptyList()))
        val repository = ReviewRepositoryImpl(api)

        val result = repository.deleteReview(1, 1)

        assertTrue(result is Result.Success)
    }

    private class FakeReviewsApi(
        private val getResponse: BaseResponse<ReviewsResponseDto> = BaseResponse(
            ReviewsResponseDto(count = 0, pageIndex = 1, pageSize = 10, data = emptyList()),
            true,
            "",
            emptyList()
        ),
        private val submitResponse: BaseResponse<ReviewSubmitResponseWrapper> = BaseResponse(
            ReviewSubmitResponseWrapper(
                pageIndex = 1,
                pageSize = 10,
                count = 1,
                data =
                    ReviewSubmitResponseDto(
                        reviewId = 1,
                        destinationId = 1,
                        rating = 5,
                        comment = "Great",
                        updatedAtUtc = "2026-04-23T10:15:30Z",
                        averageRating = 4,
                        totalReviews = 1
                    )
            ),
            true,
            "",
            emptyList()
        ),
        private val deleteResponse: BaseResponse<Unit> = BaseResponse(
            Unit,
            true,
            "",
            emptyList()
        )
    ) : ReviewsApi {
        override suspend fun getReviewsByDestinationId(
            destinationId: Int,
            pageIndex: Int,
            pageSize: Int
        ): BaseResponse<ReviewsResponseDto> = getResponse

        override suspend fun submitReview(
            destinationId: Int,
            request: SubmitReviewRequest
        ): BaseResponse<ReviewSubmitResponseWrapper> = submitResponse

        override suspend fun deleteReview(
            destinationId: Int
        ): BaseResponse<Unit> = deleteResponse
    }
}
