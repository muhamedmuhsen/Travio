package com.example.domain.repository.community

import com.example.domain.model.community.CommunityPost
import kotlinx.coroutines.flow.StateFlow

interface CommunityRepository {
    val posts: StateFlow<List<CommunityPost>>
    fun getPostById(id: Int): CommunityPost?
    fun toggleLike(postId: Int)
    fun toggleBookmark(postId: Int)
    fun addPost(
        photoUri: String,
        location: String,
        description: String
    )

    fun addComment(
        postId: Int,
        commentText: String,
        authorName: String
    )
}
