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

    @Test
    fun `PostWithCommentsDto toCommunityPost should use maxOf(dtoCount, listSize) for commentsCount`() {
        val commentDto = CommentDto(id = 1, content = "Test")
        val dto = com.example.network.dto.community.PostWithCommentsDto(
            postId = 1,
            authorId = null,
            authorName = null,
            authorAvatarUrl = null,
            location = null,
            createdAt = null,
            content = null,
            imageUrls = null,
            likesCount = 0,
            commentsCount = 0, // DTO says 0
            isLiked = false,
            commentDto = listOf(commentDto) // but list has 1
        )

        val post = dto.toCommunityPost()

        assertEquals(1, post.commentsCount)
        assertEquals(1, post.comments.size)
    }

    @Test
    fun `PostDto toCommunityPost should use maxOf(dtoCount, listSize) for commentsCount`() {
        val commentDto = CommentDto(id = 1, content = "Test")
        val dto = com.example.network.dto.community.PostDto(
            postId = 1,
            authorId = null,
            authorName = null,
            authorAvatarUrl = null,
            location = null,
            createdAt = null,
            content = null,
            imageUrls = null,
            likesCount = 0,
            commentsCount = 0, // DTO says 0
            isLiked = false,
            commentDto = listOf(commentDto) // but list has 1
        )

        val post = dto.toCommunityPost()

        assertEquals(1, post.commentsCount)
        assertEquals(1, post.comments.size)
    }

    @Test
    fun `PostDto toCommunityPost should handle null commentDto safely`() {
        val dto = com.example.network.dto.community.PostDto(
            postId = 1,
            authorId = null,
            authorName = null,
            authorAvatarUrl = null,
            location = null,
            createdAt = null,
            content = null,
            imageUrls = null,
            likesCount = 0,
            commentsCount = 5,
            isLiked = false,
            commentDto = null // Null list
        )

        val post = dto.toCommunityPost()

        assertEquals(5, post.commentsCount)
        org.junit.Assert.assertTrue(post.comments.isEmpty())
    }

    @Test
    fun `CommentDto toComment should handle null content safely`() {
        val dto = CommentDto(
            id = 1,
            content = null
        )

        val comment = dto.toComment()

        assertEquals("", comment.text)
    }
}
