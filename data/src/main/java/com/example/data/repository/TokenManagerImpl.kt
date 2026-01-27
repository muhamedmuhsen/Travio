package com.example.data.repository

import android.util.Log
import com.auth0.android.jwt.DecodeException
import com.auth0.android.jwt.JWT
import com.example.domain.utils.Result
import com.example.data.local.datastore.SecureTokenStorage
import com.example.domain.model.DecodedToken
import com.example.domain.repository.auth.TokenManager
import com.example.domain.utils.DataError
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManagerImpl @Inject constructor(private val secureTokenStorage: SecureTokenStorage) :
    TokenManager {

    override suspend fun decodeToken(): Result<DecodedToken, DataError> {
        try {
            val token = secureTokenStorage.getAccessToken()
                ?: return Result.Error(DataError.TokenError.TokenNotFound)
            val jwt = JWT(token)
            val result = DecodedToken(
                userId = (jwt.subject ?: jwt.getClaim("userId").asString()),
                expiresAt = jwt.expiresAt,
                claims = jwt.claims.mapValues { it.value.asObject(Any::class.java) })
            Log.d("Logout", "Decoded token: $result")
            return Result.Success(result)
        } catch (_: DecodeException) {
            return Result.Error(DataError.TokenError.DecodingFailed)
        } catch (_: Exception) {
            return Result.Error(DataError.Data.UnknownError)
        }
    }

    override suspend fun isTokenExpired(): Result<Boolean, DataError> {
        return when (val result = decodeToken()) {
            is Result.Error -> Result.Error(result.error)
            is Result.Success -> {
                val expiresAt =
                    result.data.expiresAt ?: return Result.Error(DataError.TokenError.InvalidToken)
                Result.Success(expiresAt.before(Date()))
            }
        }
    }


    override suspend fun getTokenClaims(): Result<Map<String, Any?>, DataError> {
        return when (val result = decodeToken()) {
            is Result.Error -> Result.Error(DataError.TokenError.CouldNotGetClaims)
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