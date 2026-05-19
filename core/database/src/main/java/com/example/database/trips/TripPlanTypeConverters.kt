package com.example.database.trips

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class TripPlanTypeConverters {
    @TypeConverter
    fun fromHotelList(value: List<TripHotelEntity>?): String {
        return value?.let { Json.encodeToString(it) } ?: "[]"
    }

    @TypeConverter
    fun toHotelList(value: String?): List<TripHotelEntity> {
        return value?.let { Json.decodeFromString(it) } ?: emptyList()
    }

    @TypeConverter
    fun fromDayList(value: List<TripDayEntity>?): String {
        return value?.let { Json.encodeToString(it) } ?: "[]"
    }

    @TypeConverter
    fun toDayList(value: String?): List<TripDayEntity> {
        return value?.let { Json.decodeFromString(it) } ?: emptyList()
    }

    @TypeConverter
    fun fromActivityList(value: List<TripActivityEntity>?): String {
        return value?.let { Json.encodeToString(it) } ?: "[]"
    }

    @TypeConverter
    fun toActivityList(value: String?): List<TripActivityEntity> {
        return value?.let { Json.decodeFromString(it) } ?: emptyList()
    }
}
