package com.example.domain.usecase.favorite.post

import com.example.domain.model.favorite.Post
import com.example.domain.repository.favorite.FavoritePostRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllPostsUseCase @Inject constructor(
    private val repository: FavoritePostRepository
) {
    operator fun invoke(): Flow<List<Post>> {
        return repository.getFavoritePosts()
    }
}