package com.example.data.repository

import android.util.Log
import com.example.data.local.datastore.SecureTokenStorage
import com.example.domain.repository.auth.AuthRepository
import com.example.domain.repository.auth.TokenManager
import com.example.domain.repository.prefernces.CredentialsManager
import com.example.domain.repository.prefernces.PreferencesManager
import com.example.domain.utils.DataError
import com.example.network.api.AuthApi
import com.example.network.dto.auth.forgetpassword.ForgetPasswordRequest
import com.example.network.dto.auth.forgetpassword.ResetPasswordRequest
import com.example.network.dto.auth.forgetpassword.VerificationCodeRequest
import com.example.network.dto.auth.login.LoginRequest
import com.example.network.dto.auth.signup.SignupRequest
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import com.example.domain.utils.Result
import com.example.network.dto.auth.GoogleLoginRequest
import com.example.network.dto.auth.logout.LogoutRequest

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val secureTokenStorage: SecureTokenStorage,
    private val credentialsManager: CredentialsManager,
    private val preferencesManager: PreferencesManager,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun login(
        email: String, password: String
    ): Result<Unit, DataError> {
        try {

            val response = api.login(LoginRequest(email, password))
            Log.d("Login", "Token: ${response.token}")

            secureTokenStorage.saveTokens(
                response.token,
                response.refreshToken
            )
            preferencesManager.setLoggedIn(true)

            return Result.Success(Unit)
        } catch (_: UnknownHostException) {
            return Result.Error(DataError.Network.NoInternetConnection)
        } catch (_: SocketTimeoutException) {
            return Result.Error(DataError.Network.Timeout)
        } catch (e: HttpException) {
            val error = when (e.code()) {
                400 -> DataError.Network.BadRequest
                401 -> DataError.Authentication.UnauthorizedAccess
                404 -> DataError.Authentication.UserNotFound
                408 -> DataError.Network.Timeout
                429 -> DataError.Network.TooManyRequests
                in 500..599 -> DataError.Network.ServerError
                else -> DataError.Network.UnexpectedResponse
            }
            return Result.Error(error)
        } catch (_: IOException) {
            return Result.Error(DataError.Network.NoInternetConnection)
        } catch (_: Exception) {
            return Result.Error(DataError.Network.UnexpectedResponse)
        }
    }

    override suspend fun signup(
        email: String, password: String, username: String, firstname: String, lastname: String
    ): Result<Unit, DataError> {
        try {
            val response =
                api.signup(SignupRequest(email = email, username = username, password = password))

            secureTokenStorage.saveTokens(
                response.user.accessToken,
                response.user.refreshToken
            )
            preferencesManager.setLoggedIn(true)
            return Result.Success(Unit)
        } catch (_: UnknownHostException) {
            return Result.Error(DataError.Network.NoInternetConnection)
        } catch (_: SocketTimeoutException) {
            return Result.Error(DataError.Network.Timeout)
        } catch (e: HttpException) {
            val error = when (e.code()) {
                400 -> DataError.Network.BadRequest
                401 -> DataError.Authentication.UnauthorizedAccess
                404 -> DataError.Authentication.UserNotFound
                408 -> DataError.Network.Timeout
                429 -> DataError.Network.TooManyRequests
                in 500..599 -> DataError.Network.ServerError
                else -> DataError.Network.UnexpectedResponse
            }
            return Result.Error(error)
        } catch (_: IOException) {
            return Result.Error(DataError.Network.NoInternetConnection)
        } catch (_: Exception) {
            return Result.Error(DataError.Network.UnexpectedResponse)
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<Unit, DataError> {
        try {
            Log.d("GoogleSignIn", "idToken: $idToken")
            val request = GoogleLoginRequest(idToken)
            val response = api.googleLogin(request)
            Log.d("GoogleSignIn", "response: $response")

            secureTokenStorage.saveTokens(
                response.token,
                response.refreshToken
            )
            preferencesManager.setLoggedIn(true)

            return Result.Success(Unit)
        } catch (_: UnknownHostException) {
            return Result.Error(DataError.Network.NoInternetConnection)
        } catch (_: SocketTimeoutException) {
            return Result.Error(DataError.Network.Timeout)
        } catch (e: HttpException) {
            val error = when (e.code()) {
                400 -> DataError.Network.BadRequest
                401 -> DataError.Authentication.UnauthorizedAccess
                404 -> DataError.Authentication.UserNotFound
                408 -> DataError.Network.Timeout
                429 -> DataError.Network.TooManyRequests
                in 500..599 -> DataError.Network.ServerError
                else -> DataError.Network.UnexpectedResponse
            }
            return Result.Error(error)
        } catch (_: IOException) {
            return Result.Error(DataError.Network.NoInternetConnection)
        } catch (_: Exception) {
            return Result.Error(DataError.Network.UnexpectedResponse)
        }
    }

    override suspend fun signInWithFacebook(accessToken: String): Result<Unit, DataError> {
        try {


//            secureTokenStorage.saveTokens(
//                response.tokenDto.token,
//                response.tokenDto.refreshTokenExpiration
//            )
//            preferencesManager.setLoggedIn(true)

            return Result.Success(Unit)
        } catch (_: UnknownHostException) {
            return Result.Error(DataError.Network.NoInternetConnection)
        } catch (_: SocketTimeoutException) {
            return Result.Error(DataError.Network.Timeout)
        } catch (e: HttpException) {
            val error = when (e.code()) {
                400 -> DataError.Network.BadRequest
                401 -> DataError.Authentication.UnauthorizedAccess
                404 -> DataError.Authentication.UserNotFound
                408 -> DataError.Network.Timeout
                429 -> DataError.Network.TooManyRequests
                in 500..599 -> DataError.Network.ServerError
                else -> DataError.Network.UnexpectedResponse
            }
            return Result.Error(error)
        } catch (_: IOException) {
            return Result.Error(DataError.Network.NoInternetConnection)
        } catch (_: Exception) {
            return Result.Error(DataError.Network.UnexpectedResponse)
        }
    }

    private suspend fun handlePostLogoutPersistence() {
        secureTokenStorage.clearTokens()
        preferencesManager.setLoggedIn(false)
        val wasRememberMeEnabled = credentialsManager.isRememberMeEnabled()
        Log.d("Logout", "wasRememberMeEnabledRepo: $wasRememberMeEnabled")
        if (!wasRememberMeEnabled)
            credentialsManager.clearCredentials()
    }
    override suspend fun logout(): Result<Unit, DataError> {
        try {
            /*TODO: should attach the access token in the request and send the refresh token in the body*/
            val refreshToken = secureTokenStorage.getRefreshToken()
            api.logout(LogoutRequest(refreshToken))

            return Result.Success(Unit)
        } catch (_: UnknownHostException) {
            Log.d("Logout", "UnknownHostException")
            return Result.Error(DataError.Network.NoInternetConnection)
        } catch (_: SocketTimeoutException) {
            Log.d("Logout", "SocketTimeoutException")
            return Result.Error(DataError.Network.Timeout)
        } catch (e: HttpException) {
            Log.d("Logout", "HttpException: ${e.code()}")
            val error = when (e.code()) {
                400 -> DataError.Network.BadRequest
                401 -> DataError.Authentication.UnauthorizedAccess
                404 -> DataError.Authentication.UserNotFound
                408 -> DataError.Network.Timeout
                429 -> DataError.Network.TooManyRequests
                in 500..599 -> DataError.Network.ServerError
                else -> DataError.Network.UnexpectedResponse
            }
            return Result.Error(error)
        } catch (_: IOException) {
            Log.d("Logout", "IOException")
            return Result.Error(DataError.Network.NoInternetConnection)
        } catch (_: Exception) {
            Log.d("Logout", "Exception")
            return Result.Error(DataError.Network.UnexpectedResponse)
        } finally {
            handlePostLogoutPersistence()
        }
    }

    override suspend fun isAuthenticated(): Result<Boolean, DataError> {
        return Result.Success(preferencesManager.isLoggedIn())
    }

    override suspend fun refreshToken(): Result<Unit, DataError> {
//        return safeApiCall {
//            val refreshToken = tokenManager.getRefreshToken() ?: return@safeApiCall Result.Error(
//                DataError.TokenError.TokenNotFound
//            )
//
//            val response = api.refreshToken(refreshToken).execute()
//
//            if (!response.isSuccessful || response.body() == null) {
//                secureTokenStorage.clearTokens()
//                preferencesManager.setLoggedIn(false)
//                credentialsManager.clearCredentials()
//                return@safeApiCall Result.Error(DataError.TokenError.InvalidToken)
//            }
//
//            val refreshResponse = response.body()!!
//            val newRefreshToken = refreshResponse.tokenDto.refreshTokenExpiration
//            secureTokenStorage.saveTokens(
//                refreshResponse.tokenDto.token, newRefreshToken.toString()
//            )
//
//            Result.Success(Unit)
//        }
        return Result.Success(Unit)
    }

    override suspend fun forgetPassword(email: String): Result<Unit, DataError> {
        try {
            api.forgetPassword(ForgetPasswordRequest(email))
            return Result.Success(Unit)
        } catch (_: UnknownHostException) {
            return Result.Error(DataError.Network.NoInternetConnection)
        } catch (_: SocketTimeoutException) {
            return Result.Error(DataError.Network.Timeout)
        } catch (e: HttpException) {
            val error = when (e.code()) {
                400 -> DataError.Network.BadRequest
                401 -> DataError.Authentication.UnauthorizedAccess
                404 -> DataError.Authentication.UserNotFound
                408 -> DataError.Network.Timeout
                429 -> DataError.Network.TooManyRequests
                in 500..599 -> DataError.Network.ServerError
                else -> DataError.Network.UnexpectedResponse
            }
            return Result.Error(error)
        } catch (_: IOException) {
            return Result.Error(DataError.Network.NoInternetConnection)
        } catch (_: Exception) {
            return Result.Error(DataError.Network.UnexpectedResponse)
        }
    }

    override suspend fun sendVerificationCode(code: String): Result<Unit, DataError> {
        try {
            val response = api.sendVerificationCode(VerificationCodeRequest(code))
            val message = response.message

            if (message.contains("expired", ignoreCase = true)) {
                return Result.Error(DataError.Verification.CodeExpired)
            }
            if (message.contains("invalid", ignoreCase = true)) {
                return Result.Error(DataError.Verification.InvalidCode)
            }
            return Result.Success(Unit)
        } catch (_: UnknownHostException) {
            return Result.Error(DataError.Network.NoInternetConnection)
        } catch (_: SocketTimeoutException) {
            return Result.Error(DataError.Network.Timeout)
        } catch (e: HttpException) {
            val error = when (e.code()) {
                400 -> DataError.Network.BadRequest
                401 -> DataError.Authentication.UnauthorizedAccess
                404 -> DataError.Authentication.UserNotFound
                408 -> DataError.Network.Timeout
                429 -> DataError.Network.TooManyRequests
                in 500..599 -> DataError.Network.ServerError
                else -> DataError.Network.UnexpectedResponse
            }
            return Result.Error(error)
        } catch (_: IOException) {
            return Result.Error(DataError.Network.NoInternetConnection)
        } catch (_: Exception) {
            return Result.Error(DataError.Network.UnexpectedResponse)
        }
    }

    override suspend fun resetPassword(newPassword: String): Result<Unit, DataError> {
        try {
            api.resetPassword(ResetPasswordRequest(newPassword))
            return Result.Success(Unit)
        } catch (_: UnknownHostException) {
            return Result.Error(DataError.Network.NoInternetConnection)
        } catch (_: SocketTimeoutException) {
            return Result.Error(DataError.Network.Timeout)
        } catch (e: HttpException) {
            val error = when (e.code()) {
                400 -> DataError.Network.BadRequest
                401 -> DataError.Authentication.UnauthorizedAccess
                404 -> DataError.Authentication.UserNotFound
                408 -> DataError.Network.Timeout
                429 -> DataError.Network.TooManyRequests
                in 500..599 -> DataError.Network.ServerError
                else -> DataError.Network.UnexpectedResponse
            }
            return Result.Error(error)
        } catch (_: IOException) {
            return Result.Error(DataError.Network.NoInternetConnection)
        } catch (_: Exception) {
            return Result.Error(DataError.Network.UnexpectedResponse)
        }
    }
}