package com.example.domain.usecase.community

import com.example.domain.model.community.CommunityPost
import com.example.domain.repository.community.CommunityRepository
import javax.inject.Inject

class GetPostByIdUseCase @Inject constructor(
    private val repository: CommunityRepository
) {
    operator fun invoke(postId: Int): CommunityPost? = repository.getPostById(postId)
}
