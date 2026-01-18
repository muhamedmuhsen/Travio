package com.example.domain.usecase.auth

import com.example.domain.utils.Result
import com.example.domain.repository.auth.AuthRepository
import com.example.domain.utils.DataError
import javax.inject.Inject

class SendVerificationCodeUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(code: String): Result<Unit, DataError> {
        if (code.length != 6) return Result.Error(DataError.Validation.MissingFields)
        return authRepository.sendVerificationCode(code)
    }
}