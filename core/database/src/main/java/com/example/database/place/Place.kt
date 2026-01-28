package com.example.database.place

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "place")
data class Place(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String,
    val imageUrl: String,
    val rating: Double,
)
