package com.example.domain.usecase.community

import com.example.domain.repository.community.CommunityRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import javax.inject.Inject

class AddCommunityPostUseCase @Inject constructor(
    private val repository: CommunityRepository
) {
    suspend operator fun invoke(
        location: String,
        description: String
    ): Result<Int, DataError> = repository.addPost(location, description)
}
