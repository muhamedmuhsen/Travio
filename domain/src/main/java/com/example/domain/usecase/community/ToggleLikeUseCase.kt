package com.example.domain.usecase.community

import com.example.domain.repository.community.CommunityRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import javax.inject.Inject

class ToggleLikeUseCase @Inject constructor(
    private val repository: CommunityRepository
) {
    suspend operator fun invoke(
        postId: String,
        isCurrentlyLiked: Boolean
    ): Result<Unit, DataError> = repository.toggleLike(postId, isCurrentlyLiked)
}
