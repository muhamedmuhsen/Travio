package com.example.data.mapper.community

import com.example.network.dto.community.CommentDto
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class CommunityMapperTest {

    @Test
    fun `should parse comment creation date as UTC even without suffix`() {
        val dto = CommentDto(
            id = 1,
            authorName = "Alex John",
            content = "Cool!",
            createdAt = "2026-05-27T15:00:00"
        )

        val comment = dto.toComment()

        // Verify it's parsed as 15:00 UTC
        assertEquals(Instant.parse("2026-05-27T15:00:00Z"), comment.createdAt)
    }

    @Test
    fun `should parse comment creation date with Z suffix correctly`() {
        val dto = CommentDto(
            id = 1,
            authorName = "Alex John",
            content = "Cool!",
            createdAt = "2026-05-27T15:00:00Z"
        )

        val comment = dto.toComment()

        assertEquals(Instant.parse("2026-05-27T15:00:00Z"), comment.createdAt)
    }
}
