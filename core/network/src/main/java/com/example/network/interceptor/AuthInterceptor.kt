package com.example.network.interceptor

import com.example.domain.repository.Auth.TokenProvider
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(private val tokenProvider: TokenProvider) :
    Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val path = originalRequest.url.encodedPath

        if (path.contains("/login") || path.contains("/register")) {
            return chain.proceed(originalRequest)
        }

        val token = runBlocking { tokenProvider.getToken() }

        val request = if (!token.isNullOrBlank()) {
            chain.request().newBuilder().addHeader("Authorization", "Bearer $token").build()
        } else {
            chain.request()
        }
        return chain.proceed(request)
    }
}