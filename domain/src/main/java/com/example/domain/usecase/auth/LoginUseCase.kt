package com.example.domain.usecase.auth

import com.example.domain.utils.Result
import com.example.domain.repository.auth.AuthRepository
import com.example.domain.utils.DataError
import com.example.domain.validators.ValidateEmailUseCase
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository,
    private val validateEmailUseCase: ValidateEmailUseCase,
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        isRememberMeChecked: Boolean
    ): Result<Unit, DataError> {
        if (email.isBlank() || password.isBlank()) {
            return Result.Error(DataError.Validation.MissingFields)
        }

        if (!validateEmailUseCase(email)) {
            return Result.Error(DataError.Validation.InvalidEmailFormat)
        }

        if (password.isBlank()) {
            return Result.Error(DataError.Validation.MissingFields)
        }

        return repository.login(email, password, isRememberMeChecked)
    }
}