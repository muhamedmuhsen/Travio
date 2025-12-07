package com.example.domain.usecase.auth

import com.example.domain.repository.auth.AuthRepository

class ResetPasswordUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(newPassword: String) {
        authRepository.resetPassword(newPassword)
    }
}