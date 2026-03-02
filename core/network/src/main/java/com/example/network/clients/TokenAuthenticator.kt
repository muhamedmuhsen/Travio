package com.example.network.clients

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
import timber.log.Timber
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class TokenAuthenticator(
    private val tokenProvider: TokenProvider,
    private val authApi: Provider<AuthApi>,
    private val sessionEventBus: SessionEventBus
) : Authenticator {

    private val mutex = Mutex()

    override fun authenticate(
        route: Route?,
        response: Response
    ): Request? {
        Timber.d("authenticate() called | code=${response.code}")

        @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
        if (response.request.tag(java.lang.Boolean::class.java) == true) {
            Timber.d("Already retried this request, giving up")
            return null
        }

        if (response.code != 401) return null

        return runBlocking {
            mutex.withLock {
                try {
                    val currentToken = tokenProvider.getAccessTokenSync()
                    val requestToken = response.request.header("Authorization")
                        ?.removePrefix("Bearer ")

                    if (currentToken != null && currentToken != requestToken) {
                        Timber.d("Using token already refreshed by another request")
                        return@runBlocking newRequestWithToken(response.request, currentToken)
                    }

                    val refreshToken = tokenProvider.getRefreshToken()
                    if (refreshToken == null) {
                        Timber.w("No refresh token available — expiring session")
                        invalidateSession()
                        return@runBlocking null
                    }

                    Timber.d("Attempting to refresh token...")

                    val refreshResponse = try {
                        authApi.get()
                            .refreshTokenSync(RefreshTokenRequest(refreshToken))
                            .execute()
                    } catch (e: Exception) {
                        Timber.e(e, "Refresh network request failed")
                        invalidateSession()
                        return@runBlocking null
                    }

                    if (!refreshResponse.isSuccessful) {
                        Timber.e("Refresh failed | code=${refreshResponse.code()}")
                        if (refreshResponse.code() in listOf(401, 403)) {
                            invalidateSession()
                        }
                        return@runBlocking null
                    }

                    val session = refreshResponse.body()
                    if (session == null) {
                        Timber.e("Empty refresh response body")
                        invalidateSession()
                        return@runBlocking null
                    }

                    tokenProvider.saveTokens(
                        accessToken = session.token,
                        refreshToken = session.refreshToken,
                        refreshTokenExpiryEpochMs = session.refreshTokenExpiration.toLongOrNull()
                    )
                    Timber.d("Token refreshed successfully")

                    newRequestWithToken(response.request, session.token, retry = true)
                } catch (e: Exception) {
                    Timber.e(e, "Unexpected error during token refresh")
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
        @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
        if (retry) builder.tag(java.lang.Boolean::class.java, true as java.lang.Boolean)
        return builder.build()
    }
}
