package com.example.feature.language

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocaleModule {

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context?): Context? {
        return context
    }

    @Provides
    @Singleton
    fun provideAppLocaleManager(@ApplicationContext context: Context): AppLocaleManager {
        return AppLocaleManager(context)
    }
}