package com.example.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.database.place.FavoritePlaceDao
import com.example.database.place.Place
import com.example.database.place.StringListConverter
import com.example.database.post.Comment
import com.example.database.post.FavoritePostDao
import com.example.database.post.Post
import com.example.database.recentlyviewed.RecentlyViewedDao
import com.example.database.recentlyviewed.RecentlyViewedDestination
import com.example.database.recentsearch.RecentSearch
import com.example.database.recentsearch.RecentSearchDao

@Database(
    entities = [Place::class, Post::class, Comment::class, RecentlyViewedDestination::class, RecentSearch::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(StringListConverter::class)
abstract class TravioDatabase : RoomDatabase() {
    abstract fun postDao(): FavoritePostDao
    abstract fun placeDao(): FavoritePlaceDao
    abstract fun recentlyViewedDao(): RecentlyViewedDao
    abstract fun recentSearchDao(): RecentSearchDao
}

