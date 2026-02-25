package com.example.database.post

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "post")
data class Post(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val content: String,
    val createdAt: String,
    val postLikes: Int,
    val imageUrl: String,
    val author: String
)
