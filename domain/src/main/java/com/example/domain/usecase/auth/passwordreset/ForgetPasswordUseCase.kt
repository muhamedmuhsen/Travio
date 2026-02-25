package com.example.domain.usecase.auth.passwordreset

import com.example.common.extensions.isValidEmail
import com.example.domain.repository.auth.PasswordResetRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import javax.inject.Inject

class ForgetPasswordUseCase @Inject constructor(
    private val authRepository: PasswordResetRepository
) {
    suspend operator fun invoke(email: String): Result<Unit, DataError> {
        if (email.isBlank()) return Result.Error(DataError.Validation.MissingFields)
        if (!email.isValidEmail()) {
            return Result.Error(DataError.Validation.InvalidEmailFormat)
        }
        return authRepository.forgetPassword(email)
    }
}
