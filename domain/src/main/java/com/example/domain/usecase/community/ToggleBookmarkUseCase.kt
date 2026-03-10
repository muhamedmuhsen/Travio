package com.example.domain.usecase.community

import com.example.domain.repository.community.CommunityRepository
import javax.inject.Inject

class ToggleBookmarkUseCase @Inject constructor(
    private val repository: CommunityRepository
) {
    operator fun invoke(postId: Int) = repository.toggleBookmark(postId)
}
