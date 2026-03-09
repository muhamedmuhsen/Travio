package com.example.domain.usecase.community

import com.example.domain.repository.community.CommunityRepository
import javax.inject.Inject

class AddCommunityPostUseCase @Inject constructor(
    private val repository: CommunityRepository
) {
    operator fun invoke(
        photoUri: String,
        location: String,
        description: String
    ) = repository.addPost(photoUri, location, description)
}
