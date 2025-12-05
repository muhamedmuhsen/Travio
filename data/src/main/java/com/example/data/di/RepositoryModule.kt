package com.example.data.di

import com.example.data.local.datastore.DataStoreManager
import com.example.data.local.datastore.TokenManagerImpl
import com.example.data.repository.AuthRepositoryImpl
import com.example.data.source.GoogleAuthDataSource
import com.example.domain.repository.auth.AuthRepository
import com.example.network.api.AuthApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        api: AuthApi, dataStoreManager: DataStoreManager, tokenManagerImpl: TokenManagerImpl,
        googleAuthDataSource: GoogleAuthDataSource
    ): AuthRepository {
        return AuthRepositoryImpl(api, dataStoreManager, googleAuthDataSource, tokenManagerImpl)
    }
}