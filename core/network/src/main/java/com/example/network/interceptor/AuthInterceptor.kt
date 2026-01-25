package com.example.network.interceptor

import android.util.Log
import com.example.common.auth.TokenProvider
import com.example.common.extensions.isNotNull
import com.example.common.extensions.isNull
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(private val tokenProvider: TokenProvider) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenProvider.getAccessTokenSync()
        val isLogout = chain.request().url.encodedPath.contains("Auth/Logout", ignoreCase = true)


        val request = if (token.isNotNull() && !isLogout) {
            chain.request().newBuilder().addHeader("Authorization", "Bearer $token").build()
        } else {
            chain.request()
        }

        return chain.proceed(request)
    }
}