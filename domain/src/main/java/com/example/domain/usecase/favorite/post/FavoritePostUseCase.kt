package com.example.domain.usecase.favorite.post

import com.example.domain.model.favorite.Post
import com.example.domain.repository.favorite.FavoritePostRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import javax.inject.Inject

class FavoritePostUseCase @Inject constructor(
    private val repository: FavoritePostRepository
) {
    suspend operator fun invoke(post: Post): Result<Unit, DataError.Local> {
        return repository.addPostToFavorite(post)
    }
}
