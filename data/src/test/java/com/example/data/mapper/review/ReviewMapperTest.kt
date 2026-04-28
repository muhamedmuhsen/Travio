package com.example.data.mapper.review

import com.example.network.dto.review.ReviewDto
import com.example.network.dto.review.ReviewsResponseDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class ReviewMapperTest {

    @Test
    fun `should map ReviewDto to Review correctly`() {
        val dto = ReviewDto(
            id = 1,
            authorName = "John Doe",
            authorAvatarUrl = "/images/avatar.jpg",
            rating = 4,
            content = "I loved it here!",
            createdAt = "2026-04-22T10:00:00Z",
            helpfulCount = 5,
            isOwnedByCurrentUser = true
        )

        val review = dto.toReview()

        assertEquals(1, review.id)
        assertEquals("John Doe", review.authorName)
        assertTrue(review.authorAvatarUrl?.contains("/images/avatar.jpg") == true)
        assertEquals(4, review.rating)
        assertEquals("I loved it here!", review.content)
        assertEquals(Instant.parse("2026-04-22T10:00:00Z"), review.createdAt)
        assertEquals(5, review.helpfulCount)
        assertTrue(review.isOwnedByCurrentUser)
    }

    @Test
    fun `should handle nullable fields in ReviewDto`() {
        val dto = ReviewDto(
            id = 1,
            authorName = null,
            authorAvatarUrl = null,
            rating = 3,
            content = null,
            createdAt = null,
            helpfulCount = 0,
            isOwnedByCurrentUser = false
        )

        val review = dto.toReview()

        assertEquals("", review.authorName)
        assertEquals(null, review.authorAvatarUrl)
        assertEquals("", review.content)
        assertFalse(review.isOwnedByCurrentUser)
    }

    @Test
    fun `should map ReviewsResponseDto to ReviewsPage correctly`() {
        val responseDto = ReviewsResponseDto(
            count = 50,
            pageIndex = 2,
            pageSize = 10,
            data = listOf(
                ReviewDto(
                    id = 1,
                    authorName = "John",
                    authorAvatarUrl = null,
                    rating = 5,
                    content = "Nice",
                    createdAt = null,
                    helpfulCount = 2,
                    isOwnedByCurrentUser = false
                )
            )
        )

        val page = responseDto.toReviewsPage()

        assertEquals(50, page.totalCount)
        assertEquals(2, page.pageIndex)
        assertEquals(10, page.pageSize)
        assertEquals(1, page.reviews.size)
        assertEquals(1, page.reviews[0].id)
    }
}
