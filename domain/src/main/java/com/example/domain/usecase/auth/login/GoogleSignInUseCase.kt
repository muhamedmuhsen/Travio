package com.example.domain.usecase.auth.login

import com.example.domain.repository.auth.LoginRepository
import com.example.domain.utils.Result
import com.example.domain.utils.DataError
import javax.inject.Inject

class GoogleSignInUseCase @Inject constructor(
    private val repository: LoginRepository
) {
    suspend operator fun invoke(idToken: String): Result<Unit, DataError> {
        if (idToken.isEmpty()) {
            return Result.Error(DataError.TokenError.InvalidToken)
        }
        return repository.signInWithGoogle(idToken)
    }
}