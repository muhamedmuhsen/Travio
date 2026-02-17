package com.example.domain.usecase.auth

import com.example.domain.repository.auth.AuthRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import javax.inject.Inject

class VerifyEmailUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit, DataError> {
        return repository.verifyEmail()
    }
}