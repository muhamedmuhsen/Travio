package com.example.domain.usecase.auth

import com.example.domain.utils.Result
import com.example.domain.repository.auth.AuthRepository
import com.example.domain.utils.DataError
import javax.inject.Inject

class GoogleSignInUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): Result<Unit, DataError> {
        if (idToken.isEmpty()) {
            return Result.Error(DataError.TokenError.InvalidToken)
        }
        return authRepository.signInWithGoogle(idToken)
    }
}