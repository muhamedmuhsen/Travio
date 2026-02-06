package com.example.network.clients

import android.util.Log
import com.example.domain.repository.auth.TokenProvider
import com.example.common.extensions.isNotNull
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
        private const val MAX_RETRY_COUNT = 3
    }

    override fun authenticate(route: Route?, response: Response): Request? {
        Log.d(TAG, "authenticate() called | code=${response.code}")

        if (responseCount(response) >= MAX_RETRY_COUNT) {
            Log.d(TAG, "authenticate() called | code=${response.code}")
            return null
        }

        return runBlocking {
            mutex.withLock {
                val currentToken = tokenProvider.getAccessTokenSync()

                if (
                    currentToken.isNotNull() &&
                    response.request.header("Authorization") != "Bearer $currentToken"
                ) {
                    Log.d(TAG, "Using already refreshed token")
                    return@runBlocking newRequestWithToken(response.request, currentToken)
                }

                val refreshToken = tokenProvider.getRefreshToken() ?: return@runBlocking null

                val refreshResponse = try {
                    authApi.get().refreshToken(refreshToken).execute()
                } catch (e: Exception) {
                    Log.e(TAG, "Refresh request failed", e)

                    tokenProvider.clearTokens()
                    return@runBlocking null
                }

                if (!refreshResponse.isSuccessful || refreshResponse.body() == null) {
                    Log.e(
                        TAG,
                        "Refresh failed | code=${refreshResponse.code()}"
                    )

                    tokenProvider.clearTokens()
                    return@runBlocking null
                }

                val session = refreshResponse.body()!!

                tokenProvider.saveTokens(
                    accessToken = session.token,
                    refreshToken = session.refreshToken
                )
                Log.d(TAG, "Token refreshed successfully")

                newRequestWithToken(
                    response.request,
                    session.token
                )
            }
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var currentResponse = response.priorResponse
        while (currentResponse != null) {
            count++
            currentResponse = currentResponse.priorResponse
        }
        return count
    }

    private fun newRequestWithToken(request: Request, token: String?): Request {
        return request.newBuilder().header("Authorization", "Bearer $token").build()
    }
}