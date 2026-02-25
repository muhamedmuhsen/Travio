package com.example.network.clients

import android.util.Log
import com.example.domain.repository.auth.TokenProvider
import com.example.network.api.AuthApi
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
    private val authApi: Provider<AuthApi>
) : Authenticator {

    private val mutex = Mutex()

    companion object {
        private const val TAG = "TokenAuthenticator"

        // Use a request tag to track if we've already retried this specific request
        private const val RETRY_TAG = "auth_retry"
    }

    override fun authenticate(
        route: Route?,
        response: Response
    ): Request? {
        Log.d(TAG, "authenticate() called | code=${response.code}")

        // Check if we've already tried to refresh for this request
        if (response.request.tag(Boolean::class.java) == true) {
            Log.d(TAG, "Already retried this request, giving up")
            return null
        }

        // Only handle 401 Unauthorized
        if (response.code != 401) {
            return null
        }

        return runBlocking {
            mutex.withLock {
                try {
                    // Double-check: Another thread might have already refreshed the token
                    val currentToken = tokenProvider.getAccessTokenSync()
                    val requestToken = response.request.header("Authorization")
                        ?.removePrefix("Bearer ")

                    // If we have a current token that's different from the request's token,
                    // it means another request already refreshed - use it
                    if (currentToken != null && currentToken != requestToken) {
                        Log.d(TAG, "Using refreshed token from current session")
                        return@runBlocking newRequestWithToken(response.request, currentToken)
                    }

                    // Proceed with refresh
                    val refreshToken = tokenProvider.getRefreshToken()
                    if (refreshToken == null) {
                        Log.w(TAG, "No refresh token available")
                        tokenProvider.clearTokens()
                        return@runBlocking null
                    }

                    Log.d(TAG, "Attempting to refresh token...")

                    val refreshResponse = try {
                        authApi.get().refreshToken(refreshToken).execute()
                    } catch (e: Exception) {
                        Log.e(TAG, "Refresh request failed", e)
                        tokenProvider.clearTokens()
                        return@runBlocking null
                    }

                    if (!refreshResponse.isSuccessful) {
                        Log.e(TAG, "Refresh failed | code=${refreshResponse.code()}")
                        // Only clear tokens on auth-related errors (401, 403)
                        if (refreshResponse.code() in listOf(401, 403)) {
                            tokenProvider.clearTokens()
                        }
                        return@runBlocking null
                    }

                    val session = refreshResponse.body()
                    if (session == null) {
                        Log.e(TAG, "Empty refresh response body")
                        tokenProvider.clearTokens()
                        return@runBlocking null
                    }

                    // Save new tokens
                    tokenProvider.saveTokens(
                        accessToken = session.token,
                        refreshToken = session.refreshToken
                    )
                    Log.d(TAG, "Token refreshed successfully")

                    // Return request with new token and mark as retried
                    newRequestWithToken(response.request, session.token, retry = true)
                } catch (e: Exception) {
                    Log.e(TAG, "Unexpected error during authentication", e)
                    null
                }
            }
        }
    }

    private fun newRequestWithToken(
        request: Request,
        token: String?,
        retry: Boolean = false
    ): Request {
        val builder = request.newBuilder().header("Authorization", "Bearer $token")

        if (retry) {
            builder.tag(Boolean::class.java, true)
        }

        return builder.build()
    }
}
