package com.example.domain.usecase.user_management

import com.example.domain.model.User
import com.example.domain.repository.user_management.UserManagementRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val repository: UserManagementRepository
) {
    suspend operator fun invoke(
        firstName: String,
        lastName: String,
        username: String
    ): Result<User, DataError> {
        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty()) return Result.Error(
            DataError.Validation.MissingFields
        )
        if (firstName.length < 3 || lastName.length < 3 || username.length < 3) return Result.Error(
            DataError.Validation.ShortName
        )

        return repository.updateProfile(
            firstName = firstName,
            lastName = lastName,
            username = username
        )
    }
}