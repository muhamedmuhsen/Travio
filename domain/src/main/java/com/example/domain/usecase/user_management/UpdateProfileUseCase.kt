package com.example.domain.usecase.user_management

import com.example.domain.repository.user_management.UserManagementRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import com.example.domain.utils.isError
import com.example.domain.validators.ValidateUpdateProfileUseCase
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val validateUpdateProfileUseCase: ValidateUpdateProfileUseCase,
    private val repository: UserManagementRepository
) {
    suspend operator fun invoke(
        firstName: String?,
        lastName: String?,
        email: String?,
        profilePictureUri: String?
    ): Result<Unit, DataError> {
        val validationResult =
            validateUpdateProfileUseCase(
                firstName = firstName,
                lastName = lastName,
                email = email,
                profilePictureUri = profilePictureUri
            )

        if (validationResult.isError) {
            val errorResult = validationResult as Result.Error
            return Result.Error(errorResult.error)
        }

        return repository.updateProfile(
            firstName = firstName,
            lastName = lastName,
            email = email,
            profilePictureUrl = profilePictureUri
        )
    }
}