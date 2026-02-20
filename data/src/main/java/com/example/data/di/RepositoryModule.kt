package com.example.data.di

import com.example.data.repository.auth.TokenManagerImpl
import com.example.data.repository.auth.EmailVerificationRepositoryImpl
import com.example.data.repository.auth.LoginRepositoryImpl
import com.example.data.repository.auth.PasswordResetRepositoryImpl
import com.example.data.repository.auth.SessionRepositoryImpl
import com.example.data.repository.auth.SignupRepositoryImpl
import com.example.domain.repository.auth.EmailVerificationRepository
import com.example.domain.repository.auth.LoginRepository
import com.example.domain.repository.auth.PasswordResetRepository
import com.example.domain.repository.auth.SessionRepository
import com.example.domain.repository.auth.SignupRepository
import com.example.domain.repository.auth.TokenManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindEmailVerificationRepository(
        emailVerificationRepositoryImpl: EmailVerificationRepositoryImpl
    ): EmailVerificationRepository

    @Binds
    @Singleton
    abstract fun bindPasswordResetRepository(
        passwordResetRepositoryImpl: PasswordResetRepositoryImpl
    ): PasswordResetRepository

    @Binds
    @Singleton
    abstract fun bindLoginRepository(
        loginRepositoryImpl: LoginRepositoryImpl
    ): LoginRepository

    @Binds
    @Singleton
    abstract fun bindSessionRepository(
        sessionRepositoryImpl: SessionRepositoryImpl
    ): SessionRepository

    @Binds
    @Singleton
    abstract fun bindSignupRepository(
        signupRepositoryImpl: SignupRepositoryImpl
    ): SignupRepository


    @Binds
    @Singleton
    abstract fun bindTokenManager(
        tokenManagerImpl: TokenManagerImpl
    ): TokenManager
}