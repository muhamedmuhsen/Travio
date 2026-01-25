package com.example.network.interceptor

import com.example.common.auth.TokenProvider
import com.example.common.extensions.isNotNull
import com.example.network.api.AuthApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Singleton

@Singleton
class TokenAuthenticator(
    private val tokenProvider: TokenProvider,
    private val authApi: AuthApi
) : Authenticator {
    private val mutex = Mutex()
    private val MAX_RETRY_COUNT = 2

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= MAX_RETRY_COUNT) return null

        return runBlocking {
            mutex.withLock {
                val currentToken = tokenProvider.getAccessTokenSync()

                if (currentToken.isNotNull() && response.request.header("Authorization") != "Bearer $currentToken") {
                    return@runBlocking newRequestWithToken(response.request, currentToken)
                }

                val refreshToken = tokenProvider.getRefreshToken() ?: return@runBlocking null

                val refreshResponse = try {
                    authApi.refreshToken(refreshToken).execute()
                } catch (_: Exception) {
                    tokenProvider.clearTokens()
                    return@runBlocking null
                }

                if (!refreshResponse.isSuccessful || refreshResponse.body() == null) {
                    tokenProvider.clearTokens()
                    return@runBlocking null
                }

                val session = refreshResponse.body()!!

                tokenProvider.saveTokens(
                    accessToken = session.tokenDto.token,
                    refreshToken = session.tokenDto.refreshToken
                )
                newRequestWithToken(
                    response.request,
                    session.tokenDto.token
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