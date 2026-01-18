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
        password: String,
        confirmPassword: String
    ): Result<Unit, DataError> {
        if (password != confirmPassword) {
            return Result.Error(DataError.Validation.PasswordMismatch)
        }
        if (!validatePasswordUseCase(password)) {
            return Result.Error(DataError.Validation.WeakPassword)
        }
        return authRepository.resetPassword(password)
    }
}