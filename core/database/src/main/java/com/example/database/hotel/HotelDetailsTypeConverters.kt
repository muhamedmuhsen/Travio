package com.example.database.hotel

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class HotelDetailsTypeConverters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromPhoneList(list: List<CachedHotelPhone>): String {
        return json.encodeToString(list)
    }

    @TypeConverter
    fun toPhoneList(string: String): List<CachedHotelPhone> {
        if (string.isBlank()) return emptyList()
        return try {
            json.decodeFromString(string)
        } catch (e: Exception) {
            emptyList()
        }
    }

    @TypeConverter
    fun fromFacilityList(list: List<CachedHotelFacility>): String {
        return json.encodeToString(list)
    }

    @TypeConverter
    fun toFacilityList(string: String): List<CachedHotelFacility> {
        if (string.isBlank()) return emptyList()
        return try {
            json.decodeFromString(string)
        } catch (e: Exception) {
            emptyList()
        }
    }

    @TypeConverter
    fun fromRoomList(list: List<CachedHotelRoom>): String {
        return json.encodeToString(list)
    }

    @TypeConverter
    fun toRoomList(string: String): List<CachedHotelRoom> {
        if (string.isBlank()) return emptyList()
        return try {
            json.decodeFromString(string)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
