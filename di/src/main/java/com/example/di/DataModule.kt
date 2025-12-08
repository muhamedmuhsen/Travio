package com.example.di

import android.content.Context
import com.example.data.local.datastore.DataStoreManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideDatastoreManager(@ApplicationContext context: Context): DataStoreManager {
        return DataStoreManager(context)
    }


    @WebClientId
    @Provides
    @Singleton
    fun provideWebClientId(@ApplicationContext context: Context): String {
        return context.getString(com.example.designsystem.R.string.web_server_id)
    }
}