package com.example.database.post

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritePostDao {

    @Transaction
    @Query("SELECT * FROM post")
    fun getAllFavoritePosts(): Flow<List<PostWithComments>>

    @Upsert
    suspend fun addPostToFavorite(post: Post)

    @Query("SELECT COUNT(*) > 0 FROM post WHERE id = :postId")
    fun isPostFavorite(postId: String): Flow<Boolean>

    @Delete
    suspend fun deletePostFromFavorite(post: Post)

    @Query("DELETE FROM post WHERE id = :postId")
    suspend fun deletePostById(postId: String)
}
