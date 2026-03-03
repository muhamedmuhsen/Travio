package com.example.data.repository.auth

import com.example.data.utils.safeApiCall
import com.example.domain.repository.auth.LoginRepository
import com.example.domain.repository.auth.TokenProvider
import com.example.domain.repository.prefernces.CredentialsManager
import com.example.domain.repository.prefernces.PreferencesManager
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import com.example.network.api.AuthApi
import com.example.network.dto.auth.login.LoginRequest
import com.example.network.dto.auth.social.GoogleLoginRequest
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val preferencesManager: PreferencesManager,
    private val tokenProvider: TokenProvider,
    private val credentialsManager: CredentialsManager
) : LoginRepository {

    override suspend fun login(
        email: String,
        password: String,
        isRememberMeChecked: Boolean
    ): Result<Unit, DataError> =
        safeApiCall {
            val response = api.login(LoginRequest(email, password))
            tokenProvider.saveTokens(
                accessToken = response.token,
                refreshToken = response.refreshToken,
                refreshTokenExpiryEpochMs = response.refreshTokenExpiration.toLongOrNull()
            )
            preferencesManager.setLoggedIn(true)
            if (isRememberMeChecked) {
                credentialsManager.saveCredentials(email, password, true)
            } else {
                credentialsManager.clearCredentials()
            }
        }

    override suspend fun signInWithGoogle(idToken: String): Result<Unit, DataError> =
        safeApiCall {
            val request = GoogleLoginRequest(idToken)
            val response = api.googleLogin(request)
            tokenProvider.saveTokens(
                accessToken = response.token,
                refreshToken = response.refreshToken,
                refreshTokenExpiryEpochMs = response.refreshTokenExpiration.toLongOrNull()
            )
            preferencesManager.setLoggedIn(true)
        }

    override suspend fun signInWithFacebook(accessToken: String): Result<Unit, DataError> =
        safeApiCall {
            // Implementation placeholder
        }
}
