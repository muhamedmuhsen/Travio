package com.example.domain.repository.auth

import com.example.common.errorhandler.AppError
import com.example.common.errorhandler.Result

interface GoogleSignIn {
    suspend fun signIn(webClientId: String): Result<String, AppError>
}