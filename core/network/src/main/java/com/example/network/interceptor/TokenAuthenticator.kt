package com.example.network.interceptor

import com.example.common.auth.TokenProvider
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
    private val tokenProvider: TokenProvider, private val authApi: AuthApi
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val mutex = Mutex()
        if (response.request.header("Authorization") != null && responseCount(response) >= 3) {
            return null
        }

        return runBlocking {
            val currentToken = tokenProvider.getAccessTokenSync()

            mutex.withLock {
                val newToken = tokenProvider.getAccessTokenSync()

                if (newToken != currentToken) {
                    return@runBlocking newRequestWithToken(response.request, newToken)
                }

                val refreshToken = tokenProvider.getRefreshToken() ?: return@runBlocking null

                val refreshResponse = try {
                    authApi.refreshToken(refreshToken).execute()
                } catch (_: Exception) {
                    runBlocking { tokenProvider.clearTokens() }
                    return@runBlocking null
                }

                if (refreshResponse.isSuccessful && refreshResponse.body() != null) {
                    val newSession = refreshResponse.body()!!

                    tokenProvider.saveTokens(
                        newSession.tokenDto.token,
                        "newSession.user.refreshToken"
                    )
                    return@runBlocking newRequestWithToken(
                        response.request,
                        newSession.tokenDto.token
                    )
                } else {
                    runBlocking { tokenProvider.clearTokens() }
                    return@runBlocking null
                }
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