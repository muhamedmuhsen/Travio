package com.example.domain.usecase.auth

import com.example.common.errorhandler.AppError
import com.example.common.errorhandler.Result
import com.example.domain.model.User
import com.example.domain.repository.auth.AuthRepository
import com.example.domain.validators.ValidateEmailUseCase
import com.example.domain.validators.ValidatePasswordUseCase
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository,
    private val validateEmailUseCase: ValidateEmailUseCase,
) {
    suspend operator fun invoke(email: String, password: String): Result<User, AppError> {
        /* TODO: validate input fields */
        if (!validateEmailUseCase(email)) {
            return Result.Error(AppError.Validation.InvalidEmailFormat)
        }
        if (password.isBlank()) {
            return Result.Error(AppError.Validation.InvalidInputs)
        }
        return repository.login(email, password)
    }
}