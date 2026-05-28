package com.example.domain.usecase.community

import com.example.domain.repository.community.CommunityRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import javax.inject.Inject

class DeleteCommentUseCase @Inject constructor(
    private val repository: CommunityRepository
) {
    suspend operator fun invoke(commentId: Int): Result<Unit, DataError> = repository.deleteComment(commentId)
}
