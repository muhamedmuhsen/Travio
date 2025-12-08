package com.example.domain.usecase.auth

import com.example.common.errorhandler.AppError
import com.example.common.errorhandler.Result
import com.example.domain.model.User
import com.example.domain.repository.auth.AuthRepository
import javax.inject.Inject

class GoogleSignInUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): Result<User, AppError> {
        return authRepository.signInWithGoogle(idToken)
    }
}