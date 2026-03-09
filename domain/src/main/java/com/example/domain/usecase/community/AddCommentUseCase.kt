package com.example.domain.usecase.community

import com.example.domain.repository.community.CommunityRepository
import javax.inject.Inject

class AddCommentUseCase @Inject constructor(
    private val repository: CommunityRepository
) {
    operator fun invoke(
        postId: Int,
        commentText: String,
        authorName: String
    ) = repository.addComment(postId, commentText, authorName)
}
