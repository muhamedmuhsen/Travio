package com.example.travio.di

import com.example.network.config.EnvironmentConfig
import com.example.travio.config.AppEnvironmentConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EnvironmentConfigModule {
    @Provides
    @Singleton
    fun provideEnvironmentConfig(): EnvironmentConfig {
        return AppEnvironmentConfig.fromBuildConfig()
    }
}
