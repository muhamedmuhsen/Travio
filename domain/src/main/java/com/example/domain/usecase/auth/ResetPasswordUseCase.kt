package com.example.domain.usecase.auth

import com.example.domain.repository.auth.AuthRepository
import com.example.domain.utils.DataError
import com.example.domain.validators.ValidatePasswordUseCase
import javax.inject.Inject
import com.example.domain.utils.Result

class ResetPasswordUseCase @Inject constructor(
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        resetToken: String?, email: String, password: String, confirmPassword: String
    ): Result<Unit, DataError> {
        if (password.isBlank() || confirmPassword.isBlank()) return Result.Error(DataError.Validation.MissingFields)
        if (!validatePasswordUseCase(password)) return Result.Error(DataError.Validation.WeakPassword)
        if (password != confirmPassword) return Result.Error(DataError.Validation.PasswordMismatch)
        if (resetToken.isNullOrBlank()) return Result.Error(DataError.TokenError.TokenNotFound)

        return authRepository.resetPassword(resetToken, email, password, confirmPassword)
    }
}