package com.example.domain.repository.community

import com.example.domain.model.community.CommunityPost
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import kotlinx.coroutines.flow.Flow

interface CommunityRepository {
    fun getAllPost(): Flow<Result<List<CommunityPost>, DataError>>
    suspend fun getPostById(postId: Int): Result<CommunityPost, DataError>
    suspend fun addPost(
        location: String,
        description: String
    ): Result<Int, DataError>

    suspend fun uploadPostImages(
        postId: Int,
        imageUris: List<String>
    ): Result<Unit, DataError>

    suspend fun deletePost(postId: Int): Result<Unit, DataError>
    suspend fun addComment(
        postId: Int,
        text: String,
        authorName: String
    ): Result<Unit, DataError>

    suspend fun toggleLike(postId: Int): Result<Unit, DataError>

    suspend fun toggleBookmark(postId: Int): Result<Unit, DataError>

    fun refreshPosts()
}
