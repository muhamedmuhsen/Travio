package com.example.data.di

import com.example.domain.repository.auth.AuthRepository
import com.example.domain.usecase.auth.ForgetPasswordUseCase
import com.example.domain.usecase.auth.GoogleSignInUseCase
import com.example.domain.usecase.auth.LoginUseCase
import com.example.domain.usecase.auth.ResetPasswordUseCase
import com.example.domain.usecase.auth.SignupUseCase
import com.example.domain.validators.ValidateEmailUseCase
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
    fun provideGoogleSignInUseCase(authRepository: AuthRepository): GoogleSignInUseCase =
        GoogleSignInUseCase(authRepository)

    @Provides
    fun provideValidateEmailUseCase(): ValidateEmailUseCase = ValidateEmailUseCase()

    @Provides
    fun provideForgetPasswordUseCase(
        validateEmailUseCase: ValidateEmailUseCase,
        authRepository: AuthRepository
    ): ForgetPasswordUseCase =
        ForgetPasswordUseCase(validateEmailUseCase, authRepository)

    @Provides
    fun provideResetPasswordUseCase(authRepository: AuthRepository): ResetPasswordUseCase =
        ResetPasswordUseCase(authRepository)
}


