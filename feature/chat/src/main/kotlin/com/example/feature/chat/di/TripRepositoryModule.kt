package com.example.feature.chat.di

import com.example.feature.chat.data.repository.TripRepositoryImpl
import com.example.feature.chat.domain.repository.TripRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TripRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTripRepository(tripRepositoryImpl: TripRepositoryImpl): TripRepository
}
