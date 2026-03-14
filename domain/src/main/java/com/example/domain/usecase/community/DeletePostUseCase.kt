package com.example.domain.usecase.community

import com.example.domain.repository.community.CommunityRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import javax.inject.Inject

class DeletePostUseCase @Inject constructor(
    private val repository: CommunityRepository
) {
    suspend operator fun invoke(postId: String): Result<Unit, DataError> =
        repository.deletePost(postId)
}
