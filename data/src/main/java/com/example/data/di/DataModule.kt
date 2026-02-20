package com.example.data.di

import android.content.Context
import com.example.common.auth.TokenProvider
import com.example.data.local.datastore.CredentialsManagerImpl
import com.example.data.local.datastore.EncryptionManager
import com.example.data.local.datastore.PreferencesManagerImpl
import com.example.data.local.datastore.SecureTokenStorage
import com.example.data.repository.auth.GoogleCredentialDataSource
import com.example.data.repository.auth.GoogleCredentialDataSourceImpl
import com.example.data.BuildConfig
import com.example.data.repository.user_management.UserManagementRepositoryImpl
import com.example.domain.repository.prefernces.CredentialsManager
import com.example.domain.repository.prefernces.PreferencesManager
import com.example.domain.repository.user_management.UserManagementRepository
import com.example.network.api.UserManagementApi
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
    fun provideGoogleCredentialDataSource(): GoogleCredentialDataSourceImpl =
        GoogleCredentialDataSourceImpl()

    @Provides
    @Singleton
    fun provideEncryptionManager(@ApplicationContext context: Context): EncryptionManager {
        return EncryptionManager(context)
    }

    @Provides
    @Singleton
    fun provideUserManagementRepository(
        secureTokenStorage: SecureTokenStorage,
        preferencesManager: PreferencesManager,
        api: UserManagementApi,
        @ApplicationContext context: Context
    ): UserManagementRepository {
        return UserManagementRepositoryImpl(
            api = api,
            secureTokenStorage = secureTokenStorage,
            preferencesManager = preferencesManager,
            context = context
        )
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
        return CredentialsManagerImpl(context, encryptionManager)
    }

    @Provides
    @Singleton
    fun providePreferencesManager(
        @ApplicationContext context: Context
    ): PreferencesManager {
        return PreferencesManagerImpl(context)
    }


    @WebClientId
    @Provides
    @Singleton
    fun provideWebClientId(): String {
        return BuildConfig.GOOGLE_WEB_CLIENT_ID
    }
}