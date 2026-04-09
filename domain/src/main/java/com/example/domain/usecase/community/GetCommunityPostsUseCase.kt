package com.example.domain.usecase.community

import com.example.domain.repository.community.CommunityRepository
import javax.inject.Inject

class GetCommunityPostsUseCase @Inject constructor(
    private val repository: CommunityRepository
) {
    operator fun invoke() = repository.getAllPost()
}
