package com.example.domain.usecase.usermanagement

import com.example.domain.model.auth.User
import com.example.domain.repository.usermanagement.UserManagementRepository
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
        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty()) {
            return Result.Error(
                DataError.Validation.MissingFields
            )
        }
        if (firstName.length < 3) return Result.Error(DataError.Validation.ShortFirstName)
        if (lastName.length < 3) return Result.Error(DataError.Validation.ShortLastName)
        if (username.length < 3) return Result.Error(DataError.Validation.ShortUsername)

        return repository.updateProfile(
            firstName = firstName,
            lastName = lastName,
            username = username
        )
    }
}
