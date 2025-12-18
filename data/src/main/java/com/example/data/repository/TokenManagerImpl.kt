package com.example.data.repository

import com.auth0.android.jwt.DecodeException
import com.auth0.android.jwt.JWT
import com.example.common.errorhandler.AppError
import com.example.common.errorhandler.Result
import com.example.data.local.datastore.SecureTokenStorage
import com.example.domain.model.DecodedToken
import com.example.domain.repository.auth.TokenManager
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManagerImpl @Inject constructor(private val secureTokenStorage: SecureTokenStorage) :
    TokenManager {

    override suspend fun decodeToken(): Result<DecodedToken, AppError> {
        return try {
            val token = secureTokenStorage.getAccessToken()
                ?: return Result.Error(AppError.TokenError.TokenNotFound)
            val jwt = JWT(token)
            Result.Success(
                DecodedToken(
                    userId = (jwt.subject ?: jwt.getClaim("userId").asString()),
                    expiresAt = jwt.expiresAt,
                    claims = jwt.claims.mapValues { it.value.asObject(Any::class.java) })
            )
        } catch (_: DecodeException) {
            Result.Error(AppError.TokenError.DecodingFailed)
        } catch (e: Exception) {
            val message = e.localizedMessage ?: "Unknown error occurred"
            Result.Error(AppError.Unknown(message))
        }
    }

    override suspend fun isTokenExpired(): Result<Boolean, AppError> {
        return when (val result = decodeToken()) {
            is Result.Error -> Result.Error(result.error)
            is Result.Success -> {
                val expiresAt =
                    result.data.expiresAt ?: return Result.Error(AppError.TokenError.InvalidToken)
                Result.Success(expiresAt.before(Date()))
            }
        }
    }


    override suspend fun getTokenClaims(): Result<Map<String, Any?>, AppError> {
        return when (val result = decodeToken()) {
            is Result.Error -> Result.Error(AppError.TokenError.CouldNotGetClaims)
            is Result.Success -> Result.Success(result.data.claims)
        }
    }

    override suspend fun getAccessToken(): String? {
        return secureTokenStorage.getAccessToken()
    }

    override suspend fun getRefreshToken(): String? {
        return secureTokenStorage.getRefreshToken()
    }

    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        secureTokenStorage.saveTokens(accessToken, refreshToken)
    }

    override suspend fun clearTokens() {
        secureTokenStorage.clearTokens()
    }

    override fun getAccessTokenSync(): String? {
        return secureTokenStorage.getAccessTokenSync()
    }
}