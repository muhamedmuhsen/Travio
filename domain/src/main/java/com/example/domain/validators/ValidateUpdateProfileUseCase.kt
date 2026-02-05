package com.example.domain.validators

import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import javax.inject.Inject

class ValidateUpdateProfileUseCase @Inject constructor(
    private val validateEmailUseCase: ValidateEmailUseCase
) {
    operator fun invoke(
        firstName: String?,
        lastName: String?,
        email: String?,
        profilePictureUri: String?
    ): Result<Boolean, DataError.Validation> {

        // Check if we have at least one non-blank field
        val hasAtLeastOneFieldToUpdate =
            !firstName.isNullOrBlank() ||
                    !lastName.isNullOrBlank() ||
                    !email.isNullOrBlank() ||
                    !profilePictureUri.isNullOrBlank()

        if (!hasAtLeastOneFieldToUpdate) {
            return Result.Error(DataError.Validation.MustHaveAtLeastOneFieldToUpdate)
        }

        // Validate email if provided and not blank
        if (!email.isNullOrBlank() && !validateEmailUseCase(email)) {
            return Result.Error(DataError.Validation.InvalidEmailFormat)
        }

        // Validate first name if provided and not blank
        if (!firstName.isNullOrBlank() && firstName.length <= 2) {
            return Result.Error(DataError.Validation.ShortName)
        }

        // Validate last name if provided and not blank
        if (!lastName.isNullOrBlank() && lastName.length <= 2) {
            return Result.Error(DataError.Validation.ShortName)
        }

        // Validate profile picture URI if provided and not blank
        if (!profilePictureUri.isNullOrBlank() && !profilePictureUri.startsWith("content://")) {
            return Result.Error(DataError.Validation.InvalidUri)
        }

        return Result.Success(true)
    }
}