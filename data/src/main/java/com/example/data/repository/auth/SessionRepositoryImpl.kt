package com.example.data.repository.auth

import com.example.data.utils.safeApiCall
import com.example.domain.repository.auth.SessionRepository
import com.example.domain.repository.auth.TokenProvider
import com.example.domain.repository.prefernces.CredentialsManager
import com.example.domain.repository.prefernces.PreferencesManager
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import com.example.network.api.AuthApi
import com.example.network.dto.auth.logout.LogoutRequest
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val credentialsManager: CredentialsManager,
    private val preferencesManager: PreferencesManager,
    private val tokenProvider: TokenProvider
) : SessionRepository {

    override suspend fun logout(): Result<Unit, DataError> {
        val refreshToken = tokenProvider.getRefreshToken()
        val result = safeApiCall {
            api.logout(LogoutRequest(refreshToken))
        }
        tokenProvider.clearTokens()
        preferencesManager.setLoggedIn(false)
        if (!credentialsManager.isRememberMeEnabled()) {
            credentialsManager.clearCredentials()
        }
        return result
    }

    override suspend fun isAuthenticated(): Result<Boolean, DataError> {
        return Result.Success(preferencesManager.isLoggedIn())
    }

    override suspend fun refreshToken(): Result<Unit, DataError> {
        TODO("not yet implemented")
    }
}
