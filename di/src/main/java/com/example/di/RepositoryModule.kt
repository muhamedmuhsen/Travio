package com.example.di

import android.content.Context
import com.example.data.repository.TokenManagerImpl
import com.example.data.repository.AuthRepositoryImpl
import com.example.data.repository.GoogleSignInImpl
import com.example.domain.repository.auth.AuthRepository
import com.example.domain.repository.auth.GoogleSignIn
import com.example.domain.repository.auth.TokenManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindTokenManager(tokenManagerImpl: TokenManagerImpl): TokenManager

}

