package com.example.domain.repository.favorite

import com.example.domain.model.favorite.Post
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import kotlinx.coroutines.flow.Flow

interface FavoritePostRepository {
    fun getFavoritePosts(): Flow<List<Post>>
    fun isPostFavorite(postId: String): Flow<Boolean>
    suspend fun addPostToFavorite(post: Post): Result<Unit, DataError.Local>
    suspend fun deletePostFromFavorite(postId: String): Result<Unit, DataError.Local>
}
