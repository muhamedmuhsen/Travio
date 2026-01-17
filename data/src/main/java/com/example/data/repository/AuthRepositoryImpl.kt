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
            Log.d("Login", "Trying to login with email: $email and password: $password")
            val response = api.login(LoginRequest(email, password))
            Log.d("Login", "Response received: $response")
//            if (!response.status) {
//                return@safeApiCall Result.Error(AppError.Authentication.InvalidCredentials)
//            }
            val domainResponse = response.toDomain()
            Log.d("Login", "Domain response received: $domainResponse")

            Log.d("Login", "Saving tokens")
            Log.d("Login", "Token: ${response.token}")
            Log.d("Login", "Refresh Token: ${response.refreshTokenExpiration}")
            secureTokenStorage.saveTokens(
                response.token, response.refreshTokenExpiration.toString()
            )
            preferencesManager.setLoggedIn(true)
            Log.d("Login", "Mapping user")
            val user = User("")
            Log.d("Login", "Returning success with user: $user")
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

            Log.d("GoogleSignIn", "trying sending the token to the backend")
            val response = api.socialLogin(request.token) // error occurred here
            Log.d("GoogleSignIn", "response received from the backend: $response")
            if (!response.status) {
                Log.d("GoogleSignIn", "error occurred while sending the token to the backend")
                return@safeApiCall Result.Error(AppError.Authentication.SignInFailed)
            }
            Log.d("GoogleSignIn", "sending it")
            secureTokenStorage.saveTokens(
                response.tokenDto.token, response.tokenDto.refreshTokenExpiration.toString()
            )
            preferencesManager.setLoggedIn(true)

            Result.Success(User(""))
        }
    }

    override suspend fun signInWithFacebook(accessToken: String): Result<String, AppError> {
        return safeApiCall {
            val request = SocialLoginRequest(provider = Provider.FACEBOOK, token = accessToken)
            val response = api.socialLogin(request.provider.name)

            if (!response.status) {
                return@safeApiCall Result.Error(AppError.Authentication.SignInFailed)
            }

            /*TODO: send the token to the backend*/
            secureTokenStorage.saveTokens(
                response.tokenDto.token, response.tokenDto.refreshTokenExpiration.toString()
            )
            preferencesManager.setLoggedIn(true)

            Result.Success(response.tokenDto.token)
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
            val newRefreshToken = refreshResponse.tokenDto.refreshTokenExpiration
            secureTokenStorage.saveTokens(
                refreshResponse.tokenDto.token,
                newRefreshToken.toString()
            )

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
                    response.message?.contains(
                        "expired",
                        ignoreCase = true
                    ) == true -> AppError.Verification.CodeExpired

                    response.message?.contains(
                        "invalid",
                        ignoreCase = true
                    ) == true -> AppError.Verification.InvalidCode

                    response.message?.contains(
                        "attempts",
                        ignoreCase = true
                    ) == true -> AppError.Verification.TooManyAttempts

                    else -> AppError.Verification.VerificationFailed(
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