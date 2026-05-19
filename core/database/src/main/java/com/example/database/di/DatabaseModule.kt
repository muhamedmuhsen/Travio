package com.example.database.di

import android.content.Context
import androidx.room.Room
import com.example.database.TravioDatabase
import com.example.database.place.FavoritePlaceDao
import com.example.database.post.FavoritePostDao
import com.example.database.recentlyviewed.RecentlyViewedDao
import com.example.database.recentsearch.RecentSearchDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TravioDatabase =
        Room.databaseBuilder(
            context,
            TravioDatabase::class.java,
            "travio_database"
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun providePostDao(database: TravioDatabase): FavoritePostDao = database.postDao()

    @Provides
    @Singleton
    fun providePlaceDao(database: TravioDatabase): FavoritePlaceDao = database.placeDao()

    @Provides
    @Singleton
    fun provideRecentlyViewedDao(database: TravioDatabase): RecentlyViewedDao = database.recentlyViewedDao()

    @Provides
    @Singleton
    fun provideRecentSearchDao(database: TravioDatabase): RecentSearchDao = database.recentSearchDao()

    @Provides
    @Singleton
    fun provideTripPlanDao(database: TravioDatabase): com.example.database.trips.TripPlanDao = database.tripPlanDao()
}
