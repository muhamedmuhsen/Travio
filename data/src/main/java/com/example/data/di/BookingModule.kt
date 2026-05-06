package com.example.data.di

import com.example.data.repository.booking.BookingRepositoryImpl
import com.example.domain.repository.booking.BookingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BookingModule {

    @Binds
    @Singleton
    abstract fun bindBookingRepository(bookingRepositoryImpl: BookingRepositoryImpl): BookingRepository
}
