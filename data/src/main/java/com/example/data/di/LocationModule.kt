package com.example.data.di

import android.content.Context
import com.example.data.repository.destinations.LocationDataSoruce
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocationModule {
    @Provides
    @Singleton
    fun provideLocationDatasource(
        @ApplicationContext context: Context
    ): LocationDataSoruce = LocationDataSoruce(context)

}