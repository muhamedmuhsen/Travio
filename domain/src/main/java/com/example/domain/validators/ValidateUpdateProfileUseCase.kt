package com.example.domain.validators

import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import javax.inject.Inject

class ValidateUpdateProfileUseCase @Inject constructor(private val validateEmailUseCase: ValidateEmailUseCase) {
    operator fun invoke(
        firstName: String?,
        lastName: String?,
        email: String?,
        profilePictureUrl: String?
    ): Result<Boolean, DataError> {
        val hasAtLeastOneFieldToUpdate =
            !listOf(firstName, lastName, email, profilePictureUrl).all { it == null }
        if (!hasAtLeastOneFieldToUpdate) {
            return Result.Error(DataError.Validation.MustHaveAtLeastOneFieldToUpdate)
        }
        val isValidEmail = email?.let { validateEmailUseCase(it) } ?: true
        if (!isValidEmail) {
            return Result.Error(DataError.Validation.InvalidEmailFormat)
        }
        val isValidFirstName = firstName?.let { it.length > 2 } ?: true
        if (!isValidFirstName) {
            return Result.Error(DataError.Validation.ShortName)
        }
        val isValidLastName = lastName?.let { it.length > 2 } ?: true
        if (!isValidLastName) {
            return Result.Error(DataError.Validation.ShortName)
        }
        // TODO: Validate profile picture URL

        return Result.Success(true)
    }
}