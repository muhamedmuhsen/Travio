package com.example.domain.usecase.auth

import com.example.common.errorhandler.AppError
import com.example.common.errorhandler.Result
import com.example.domain.repository.login.AuthRepository

class SocialSigninUseCase(
    private val repository: AuthRepository
) {
    suspend fun signInWithGoogle(idToken: String): Result<String, AppError> {
        if (idToken.isEmpty()) {
            return Result.Error(AppError.TokenError.InvalidToken)
        }
        return repository.signInWithGoogle(idToken)
    }

    suspend fun signInWithFacebook(accessToken: String): Result<String, AppError> {
        if (accessToken.isEmpty()) {
            return Result.Error(AppError.TokenError.InvalidToken)
        }
        return repository.signInWithFacebook(accessToken)
    }
}