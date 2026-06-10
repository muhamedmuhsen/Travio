package com.example.network.di

import com.example.domain.repository.auth.TokenProvider
import com.example.domain.session.SessionEventBus
import com.example.network.api.AiApi
import com.example.network.api.AuthApi
import com.example.network.api.CommunityApi
import com.example.network.api.DestinationsApi
import com.example.network.api.FavoritesApi
import com.example.network.api.HotelApi
import com.example.network.api.ReviewsApi
import com.example.network.api.SurveyApi
import com.example.network.api.TripApi
import com.example.network.api.UserManagementApi
import com.example.network.clients.AuthInterceptor
import com.example.network.clients.TokenAuthenticator
import com.example.network.config.EnvironmentConfig
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
import java.util.concurrent.TimeUnit
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
                    chain: Array<X509Certificate>,
                    authType: String
                ) {
                }

                override fun checkServerTrusted(
                    chain: Array<X509Certificate>,
                    authType: String
                ) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            })

            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, trustAllCerts, SecureRandom())

            builder.sslSocketFactory(
                sslContext.socketFactory,
                trustAllCerts[0] as X509TrustManager
            )
            builder.hostnameVerifier { _, _ -> true }
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(environmentConfig: EnvironmentConfig): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = if (environmentConfig.enableVerboseNetworkLogs) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
            redactHeader("Authorization")
        }

    @Provides
    @Singleton
    fun provideUserManagementApi(retrofit: Retrofit): UserManagementApi {
        return retrofit.create(UserManagementApi::class.java)
    }

    @Provides
    @Singleton
    fun provideTokenAuthenticator(
        tokenProvider: TokenProvider,
        authApi: javax.inject.Provider<AuthApi>,
        sessionEventBus: SessionEventBus
    ): TokenAuthenticator {
        return TokenAuthenticator(
            tokenProvider = tokenProvider,
            authApi = authApi,
            sessionEventBus = sessionEventBus
        )
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        loggingInterceptor: HttpLoggingInterceptor,
        authenticator: TokenAuthenticator,
        environmentConfig: EnvironmentConfig
    ): OkHttpClient {
        require(!(environmentConfig.isProduction && environmentConfig.enableDebugDiagnostics)) {
            "Production environment cannot enable debug diagnostics in network client"
        }

        return OkHttpClient
            .Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .authenticator(authenticator)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .apply {
                if (environmentConfig.enableDebugDiagnostics) {
                    addUnsafeTrustManager(this)
                }
            }.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        @BaseUrl baseUrl: String,
        client: OkHttpClient
    ): Retrofit {
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
    fun provideCommunityApi(retrofit: Retrofit): CommunityApi {
        return retrofit.create(CommunityApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDestinationApi(retrofit: Retrofit): DestinationsApi {
        return retrofit.create(DestinationsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideFavoritesApi(retrofit: Retrofit): FavoritesApi {
        return retrofit.create(FavoritesApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSurveyApi(retrofit: Retrofit): SurveyApi {
        return retrofit.create(SurveyApi::class.java)
    }

    @Provides
    @Singleton
    fun provideReviewsApi(retrofit: Retrofit): ReviewsApi {
        return retrofit.create(ReviewsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideFlightBookingApi(retrofit: Retrofit): com.example.network.api.FlightBookingApi {
        return retrofit.create(com.example.network.api.FlightBookingApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAiApi(retrofit: Retrofit): AiApi {
        return retrofit.create(AiApi::class.java)
    }

    @Provides
    @Singleton
    fun provideHotelApi(retrofit: Retrofit): HotelApi {
        return retrofit.create(HotelApi::class.java)
    }

    @Provides
    @Singleton
    fun provideTripApi(retrofit: Retrofit): TripApi {
        return retrofit.create(TripApi::class.java)
    }

    @Provides
    @Singleton
    @BaseUrl
    fun provideBaseUrl(environmentConfig: EnvironmentConfig): String {
        return environmentConfig.baseUrl
    }

    @Provides
    @Singleton
    @ImageBaseUrl
    fun provideImageBaseUrl(environmentConfig: EnvironmentConfig): String {
        return environmentConfig.imageBaseUrl
    }
}
