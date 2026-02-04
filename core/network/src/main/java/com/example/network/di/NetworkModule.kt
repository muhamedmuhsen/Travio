package com.example.network.di

import com.example.common.auth.TokenProvider
import com.example.network.BuildConfig
import com.example.network.api.AuthApi
import com.example.network.api.UserManagementApi
import com.example.network.clients.AuthInterceptor
import com.example.network.clients.TokenAuthenticator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private fun addUnsafeTrustManager(builder: OkHttpClient.Builder) {
        try {
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun checkClientTrusted(
                    chain: Array<X509Certificate>, authType: String
                ) {
                }

                override fun checkServerTrusted(
                    chain: Array<X509Certificate>, authType: String
                ) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            })

            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, trustAllCerts, SecureRandom())

            builder.sslSocketFactory(
                sslContext.socketFactory, trustAllCerts[0] as X509TrustManager
            )
            builder.hostnameVerifier { _, _ -> true }
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    @Provides
    @Singleton
    fun provideUserManagementApi(retrofit: Retrofit): UserManagementApi {
        return retrofit.create(UserManagementApi::class.java)
    }

    @Provides
    @Singleton
    fun provideTokenAuthenticator(
        tokenProvider: TokenProvider,
        authApi: javax.inject.Provider<AuthApi>
    ): TokenAuthenticator {
        return TokenAuthenticator(
            tokenProvider = tokenProvider,
            authApi = authApi
        )
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        loggingInterceptor: HttpLoggingInterceptor,
        authenticator: TokenAuthenticator
    ): OkHttpClient {
        return OkHttpClient
            .Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .authenticator(authenticator)
            .apply {
                if (BuildConfig.DEBUG) {
                    addUnsafeTrustManager(this)
                }
            }.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(@BaseUrl baseUrl: String, client: OkHttpClient): Retrofit {
        return Retrofit.Builder().baseUrl(baseUrl).client(client)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }


    @Provides
    @Singleton
    @BaseUrl
    fun provideBaseUrl(): String {
        return BuildConfig.BASE_URL
    }
}