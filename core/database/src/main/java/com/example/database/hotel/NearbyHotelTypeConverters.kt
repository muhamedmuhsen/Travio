package com.example.database.hotel

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class NearbyHotelTypeConverters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromImageList(list: List<CachedHotelImage>): String {
        return json.encodeToString(list)
    }

    @TypeConverter
    fun toImageList(string: String): List<CachedHotelImage> {
        if (string.isBlank()) return emptyList()
        return try {
            json.decodeFromString(string)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
