package com.example.network.clients

import android.util.Log
import com.example.domain.repository.auth.TokenProvider
import com.example.domain.session.SessionEventBus
import com.example.network.api.AuthApi
import com.example.network.dto.auth.refresh.RefreshTokenRequest
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class TokenAuthenticator(
    private val tokenProvider: TokenProvider,
    private val authApi: Provider<AuthApi>,
    private val sessionEventBus: SessionEventBus
) : Authenticator {

    private val mutex = Mutex()

    companion object {
        private const val TAG = "TokenAuthenticator"
    }

    override fun authenticate(
        route: Route?,
        response: Response
    ): Request? {
        Log.d(TAG, "authenticate() called | code=${response.code}")

        // Check if we've already tried to refresh for this specific request
        if (response.request.tag(Boolean::class.java) == true) {
            Log.d(TAG, "Already retried this request, giving up")
            return null
        }

        // Only handle 401 Unauthorized
        if (response.code != 401) return null

        return runBlocking {
            mutex.withLock {
                try {
                    // Double-check: another coroutine may have already refreshed the token
                    val currentToken = tokenProvider.getAccessTokenSync()
                    val requestToken = response.request.header("Authorization")
                        ?.removePrefix("Bearer ")

                    if (currentToken != null && currentToken != requestToken) {
                        Log.d(TAG, "Using token already refreshed by another request")
                        return@runBlocking newRequestWithToken(response.request, currentToken)
                    }

                    val refreshToken = tokenProvider.getRefreshToken()
                    if (refreshToken == null) {
                        Log.w(TAG, "No refresh token available — expiring session")
                        invalidateSession()
                        return@runBlocking null
                    }

                    Log.d(TAG, "Attempting to refresh token...")

                    val refreshResponse = try {
                        authApi.get()
                            .refreshTokenSync(RefreshTokenRequest(refreshToken))
                            .execute()
                    } catch (e: Exception) {
                        Log.e(TAG, "Refresh network request failed", e)
                        invalidateSession()
                        return@runBlocking null
                    }

                    if (!refreshResponse.isSuccessful) {
                        Log.e(TAG, "Refresh failed | code=${refreshResponse.code()}")
                        if (refreshResponse.code() in listOf(401, 403)) {
                            invalidateSession()
                        }
                        return@runBlocking null
                    }

                    val session = refreshResponse.body()
                    if (session == null) {
                        Log.e(TAG, "Empty refresh response body")
                        invalidateSession()
                        return@runBlocking null
                    }

                    tokenProvider.saveTokens(
                        accessToken = session.token,
                        refreshToken = session.refreshToken,
                        refreshTokenExpiryEpochMs = session.refreshTokenExpiration.toLongOrNull()
                    )
                    Log.d(TAG, "Token refreshed successfully")

                    newRequestWithToken(response.request, session.token, retry = true)
                } catch (e: Exception) {
                    Log.e(TAG, "Unexpected error during token refresh", e)
                    null
                }
            }
        }
    }

    private suspend fun invalidateSession() {
        tokenProvider.clearTokens()
        sessionEventBus.emitSessionExpired()
    }

    private fun newRequestWithToken(
        request: Request,
        token: String?,
        retry: Boolean = false
    ): Request {
        val builder = request.newBuilder().header("Authorization", "Bearer $token")
        if (retry) builder.tag(Boolean::class.java, true)
        return builder.build()
    }
}
