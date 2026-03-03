package com.example.data.repository.auth

import com.example.data.utils.safeApiCall
import com.example.domain.repository.auth.SessionRepository
import com.example.domain.repository.auth.TokenManager
import com.example.domain.repository.auth.TokenProvider
import com.example.domain.repository.prefernces.CredentialsManager
import com.example.domain.repository.prefernces.PreferencesManager
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import com.example.network.api.AuthApi
import com.example.network.dto.auth.logout.LogoutRequest
import com.example.network.dto.auth.refresh.RefreshTokenRequest
import timber.log.Timber
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val credentialsManager: CredentialsManager,
    private val preferencesManager: PreferencesManager,
    private val tokenProvider: TokenProvider,
    private val tokenManager: TokenManager
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
        if (!preferencesManager.isLoggedIn()) return Result.Success(false)

        // Proactively check access-token expiry
        return when (val expired = tokenManager.isTokenExpired()) {
            is Result.Error -> {
                Timber.w("isAuthenticated: could not decode token — ${expired.error}")
                // If we can't read the token at all the session is invalid
                preferencesManager.setLoggedIn(false)
                Result.Success(false)
            }

            is Result.Success -> {
                if (!expired.data) {
                    // Token still valid
                    Result.Success(true)
                } else {
                    // Access token expired — try to refresh silently
                    Timber.d("isAuthenticated: access token expired, attempting silent refresh")
                    when (val refresh = refreshToken()) {
                        is Result.Success -> Result.Success(true)
                        is Result.Error -> {
                            Timber.w("isAuthenticated: silent refresh failed — ${refresh.error}")
                            preferencesManager.setLoggedIn(false)
                            Result.Success(false)
                        }
                    }
                }
            }
        }
    }

    override suspend fun refreshToken(): Result<Unit, DataError> {
        val refreshToken = tokenProvider.getRefreshToken()
            ?: return Result.Error(DataError.TokenError.TokenNotFound)

        // Guard: if the refresh token itself is already past its expiry, bail early
        val refreshExpiry = tokenProvider.getRefreshTokenExpiry()
        if (refreshExpiry != null && System.currentTimeMillis() > refreshExpiry) {
            Timber.w("refreshToken: refresh token is expired")
            tokenProvider.clearTokens()
            preferencesManager.setLoggedIn(false)
            return Result.Error(DataError.TokenError.ExpiredToken)
        }

        return safeApiCall {
            val response = api.refreshToken(RefreshTokenRequest(refreshToken))
            tokenProvider.saveTokens(
                accessToken = response.token,
                refreshToken = response.refreshToken,
                refreshTokenExpiryEpochMs = response.refreshTokenExpiration.toLongOrNull()
            )
            Timber.d("refreshToken: tokens saved successfully")
        }
    }
}
