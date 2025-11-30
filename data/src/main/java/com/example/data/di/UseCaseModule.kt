package com.example.data.di

import com.example.domain.repository.login.AuthRepository
import com.example.domain.usecase.auth.LoginUseCase
import com.example.domain.usecase.auth.SignupUseCase
import com.example.domain.usecase.auth.SocialSigninUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    @Provides
    fun provideLoginUseCase(authRepository: AuthRepository): LoginUseCase =
        LoginUseCase(authRepository)

    @Provides
    fun provideSignupUseCase(authRepository: AuthRepository): SignupUseCase =
        SignupUseCase(authRepository)

    @Provides
    fun provideSocialSignin(
        authRepository: AuthRepository
    ): SocialSigninUseCase = SocialSigninUseCase(authRepository)
}