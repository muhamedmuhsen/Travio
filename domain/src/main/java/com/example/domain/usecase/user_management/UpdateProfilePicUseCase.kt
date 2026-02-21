package com.example.domain.usecase.user_management

import com.example.domain.repository.user_management.UserManagementRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import javax.inject.Inject

class UpdateProfilePicUseCase @Inject constructor(private val repository: UserManagementRepository) {
    suspend operator fun invoke(uri: String): Result<String, DataError> {
        if (uri.isBlank()) return Result.Error(DataError.Validation.InvalidUri)

        return repository.updateProfilePic(uri)
    }

}