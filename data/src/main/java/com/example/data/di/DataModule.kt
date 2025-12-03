package com.example.data.di

import android.content.Context
import com.example.data.local.datastore.DataStoreManager
import com.example.domain.repository.auth.TokenProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindTokenProvider(
        dataStoreManager: DataStoreManager
    ): TokenProvider

    companion object {
        @Provides
        @Singleton
        fun bindDatastoreManager(@ApplicationContext context: Context): DataStoreManager {
            return DataStoreManager(context)
        }
    }
}