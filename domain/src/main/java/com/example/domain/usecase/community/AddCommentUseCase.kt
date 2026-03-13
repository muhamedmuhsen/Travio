package com.example.domain.usecase.community

import com.example.domain.repository.community.CommunityRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import javax.inject.Inject

class AddCommentUseCase @Inject constructor(
    private val repository: CommunityRepository
) {
    suspend operator fun invoke(
        postId: Int,
        commentText: String,
        authorName: String
    ): Result<Unit, DataError> = repository.addComment(postId, commentText, authorName)
}
