package com.example.di

import android.content.Context
import com.example.common.auth.TokenProvider
import com.example.data.local.datastore.CredentialsManager
import com.example.data.local.datastore.EncryptionManager
import com.example.data.local.datastore.PreferencesManager
import com.example.data.local.datastore.SecureTokenStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideEncryptionManager(@ApplicationContext context: Context): EncryptionManager {
        return EncryptionManager(context)
    }

    @Provides
    @Singleton
    fun provideSecureTokenStorage(
        @ApplicationContext context: Context, encryptionManager: EncryptionManager
    ): SecureTokenStorage {
        return SecureTokenStorage(context, encryptionManager)
    }

    @Provides
    @Singleton
    fun provideTokenProvider(storage: SecureTokenStorage): TokenProvider {
        return storage
    }

    @Provides
    @Singleton
    fun provideCredentialsManager(
        @ApplicationContext context: Context, encryptionManager: EncryptionManager
    ): CredentialsManager {
        return CredentialsManager(context, encryptionManager)
    }

    @Provides
    @Singleton
    fun providePreferencesManager(
        @ApplicationContext context: Context
    ): PreferencesManager {
        return PreferencesManager(context)
    }


    @WebClientId
    @Provides
    @Singleton
    fun provideWebClientId(@ApplicationContext context: Context): String {
        return context.getString(com.example.designsystem.R.string.web_server_id)
    }
}