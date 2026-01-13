package com.example.di

import com.example.domain.repository.auth.AuthRepository
import com.example.domain.repository.auth.GoogleSignIn
import com.example.domain.usecase.auth.ForgetPasswordUseCase
import com.example.domain.usecase.auth.GoogleLoginUseCase
import com.example.domain.usecase.auth.GoogleSignInUseCase
import com.example.domain.usecase.auth.LoginUseCase
import com.example.domain.usecase.auth.ResetPasswordUseCase
import com.example.domain.usecase.auth.SignupUseCase
import com.example.domain.validators.ValidateEmailUseCase
import com.example.domain.validators.ValidatePasswordUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
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
    fun provideResetPasswordUseCase(
        validatePasswordUseCase: ValidatePasswordUseCase,
        authRepository: AuthRepository
    ): ResetPasswordUseCase =
        ResetPasswordUseCase(validatePasswordUseCase, authRepository)

    @Provides
    fun provideGoogleLoginUseCase(googleSignIn: GoogleSignIn): GoogleLoginUseCase=
        GoogleLoginUseCase(googleSignIn)

}


