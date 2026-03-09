package com.example.domain.usecase.community

import com.example.domain.model.community.CommunityPost
import com.example.domain.repository.community.CommunityRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetCommunityPostsUseCase @Inject constructor(
    private val repository: CommunityRepository
) {
    operator fun invoke(): StateFlow<List<CommunityPost>> = repository.posts
}
