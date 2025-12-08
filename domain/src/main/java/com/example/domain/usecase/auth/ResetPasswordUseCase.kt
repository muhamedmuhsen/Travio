package com.example.domain.usecase.auth

import com.example.domain.repository.auth.AuthRepository
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(newPassword: String) {
        authRepository.resetPassword(newPassword)
    }
}