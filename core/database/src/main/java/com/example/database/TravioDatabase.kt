package com.example.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.database.place.Place
import com.example.database.place.FavoritePlaceDao
import com.example.database.post.Comment
import com.example.database.post.Post
import com.example.database.post.FavoritePostDao

@Database(entities = [Place::class, Post::class, Comment::class], version = 1)
abstract class TravioDatabase : RoomDatabase() {
    abstract fun postDao(): FavoritePostDao
    abstract fun placeDao(): FavoritePlaceDao
}


// TODO: mapping entities to domain model