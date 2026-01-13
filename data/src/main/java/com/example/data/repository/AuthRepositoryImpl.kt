package com.example.data.repository

import android.util.Log
import com.example.common.errorhandler.AppError
import com.example.common.errorhandler.Result
import com.example.data.helpers.safeApiCall
import com.example.data.local.datastore.CredentialsManager
import com.example.data.local.datastore.PreferencesManager
import com.example.data.local.datastore.SecureTokenStorage
import com.example.data.mapper.toDomain
import com.example.domain.model.User
import com.example.domain.repository.auth.TokenManager
import com.example.domain.repository.auth.AuthRepository
import com.example.network.api.AuthApi
import com.example.network.dto.auth.forgetpassword.ForgetPasswordRequest
import com.example.network.dto.auth.forgetpassword.ResetPasswordRequest
import com.example.network.dto.auth.forgetpassword.VerificationCodeRequest
import com.example.network.dto.auth.login.LoginRequest
import com.example.network.dto.auth.social.Provider
import com.example.network.dto.auth.signup.SignupRequest
import com.example.network.dto.auth.social.SocialLoginRequest
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val secureTokenStorage: SecureTokenStorage,
    private val credentialsManager: CredentialsManager,
    private val preferencesManager: PreferencesManager,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User, AppError> {
        return safeApiCall {
            val response = api.login(LoginRequest(email, password))

            if (!response.status) {
                return@safeApiCall Result.Error(AppError.Authentication.InvalidCredentials)
            }

            secureTokenStorage.saveTokens(response.user.accessToken, response.user.refreshToken)
            preferencesManager.setLoggedIn(true)

            val user = response.user.toDomain(/* TODO: mapping to domain model */)

            Result.Success(user)
        }
    }

    override suspend fun signup(
        email: String, password: String, username: String, firstname: String, lastname: String
    ): Result<User, AppError> {
        return safeApiCall {
            val response =
                api.signup(SignupRequest(email = email, username = username, password = password))
            if (!response.status) {
                return@safeApiCall Result.Error(AppError.Authentication.RegistrationFailed)
            }
            secureTokenStorage.saveTokens(response.user.accessToken, response.user.refreshToken)
            preferencesManager.setLoggedIn(true)

            val user = response.user.toDomain(
                //email, phone
            )
            Result.Success(user)
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<User, AppError> {
        return safeApiCall {
            val request = SocialLoginRequest(provider = Provider.GOOGLE, token = idToken)
            Log.d("GoogleSignIn", "Requesting social login with token: $idToken")

            val response = api.socialLogin(request) // error occurred here
            Log.d("GoogleSignIn", "trying sending the token to the backend")
            if (!response.status) {
                Log.d("GoogleSignIn", "error occurred while sending the token to the backend")
                return@safeApiCall Result.Error(AppError.Authentication.SignInFailed)
            }
            Log.d("GoogleSignIn", "sending it")
            secureTokenStorage.saveTokens(response.user.accessToken, response.user.refreshToken)
            preferencesManager.setLoggedIn(true)

            Result.Success(response.user.toDomain())
        }
    }

    override suspend fun signInWithFacebook(accessToken: String): Result<String, AppError> {
        return safeApiCall {
            val request = SocialLoginRequest(provider = Provider.FACEBOOK, token = accessToken)
            val response = api.socialLogin(request)

            if (!response.status) {
                return@safeApiCall Result.Error(AppError.Authentication.SignInFailed)
            }

            /*TODO: send the token to the backend*/
            secureTokenStorage.saveTokens(
                response.user.accessToken,
                response.user.refreshToken
            )
            preferencesManager.setLoggedIn(true)

            Result.Success(response.user.accessToken)
        }
    }

    override suspend fun logout(): Result<Unit, AppError> {
        return safeApiCall {
            api.logout()

            secureTokenStorage.clearTokens()
            preferencesManager.setLoggedIn(false)
            credentialsManager.clearCredentials()

            Result.Success(Unit)
        }
    }

    override suspend fun isAuthenticated(): Result<Boolean, AppError> {
        return Result.Success(preferencesManager.isLoggedIn())
    }
    override suspend fun refreshToken(): Result<Unit, AppError> {
        return safeApiCall {
            val refreshToken = tokenManager.getRefreshToken() ?: return@safeApiCall Result.Error(
                AppError.TokenError.TokenNotFound
            )

            val response = api.refreshToken(refreshToken).execute()

            if (!response.isSuccessful || response.body() == null) {
                secureTokenStorage.clearTokens()
                preferencesManager.setLoggedIn(false)
                credentialsManager.clearCredentials()
                return@safeApiCall Result.Error(AppError.TokenError.InvalidToken)
            }

            val refreshResponse = response.body()!!
            val newRefreshToken = refreshResponse.user.refreshToken
            secureTokenStorage.saveTokens(refreshResponse.user.accessToken, newRefreshToken)

            Result.Success(Unit)
        }
    }

    override suspend fun forgetPassword(email: String): Result<Unit, AppError> {
        return safeApiCall {
            val response = api.forgetPassword(ForgetPasswordRequest(email))
            if (!response.status) {
                return@safeApiCall Result.Error(AppError.Authentication.InvalidCredentials)
            }
            Result.Success(Unit)
        }
    }

    override suspend fun sendVerificationCode(code: String): Result<Unit, AppError> {
        if (code.isBlank()) {
            return Result.Error(AppError.Verification.InvalidCode)
        }
        return safeApiCall {
            val response = api.sendVerificationCode(VerificationCodeRequest(code))

            if (!response.status || !response.isVerified) {
                val error = when {
                    response.message?.contains("expired", ignoreCase = true) == true ->
                        AppError.Verification.CodeExpired

                    response.message?.contains("invalid", ignoreCase = true) == true ->
                        AppError.Verification.InvalidCode

                    response.message?.contains("attempts", ignoreCase = true) == true ->
                        AppError.Verification.TooManyAttempts

                    else ->
                        AppError.Verification.VerificationFailed(
                            response.message ?: "Verification failed"
                        )
                }
                return@safeApiCall Result.Error(error)
            }

            Result.Success(Unit)
        }
    }

    override suspend fun resetPassword(newPassword: String): Result<Unit, AppError> {
        return safeApiCall {
            val response = api.resetPassword(ResetPasswordRequest(newPassword))
            if (!response.status) {
                return@safeApiCall Result.Error(AppError.Network.BadRequest(response.message))
            }
            Result.Success(Unit)
        }
    }
}