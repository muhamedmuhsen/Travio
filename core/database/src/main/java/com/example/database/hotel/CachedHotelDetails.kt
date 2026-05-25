package com.example.database.hotel

import androidx.room.Entity
import kotlinx.serialization.Serializable

@Entity(
    tableName = "hotel_details",
    primaryKeys = ["code", "searchCheckIn", "searchCheckOut", "searchAdults", "searchChildren", "searchChildrenAges"]
)
data class CachedHotelDetails(
    val code: Int,
    val name: String,
    val description: String?,
    val categoryName: String?,
    val accommodationType: String?,
    val address: String?,
    val city: String?,
    val countryCode: String?,
    val latitude: Double?,
    val longitude: Double?,
    val email: String?,
    val web: String?,
    val phones: List<CachedHotelPhone>,
    val images: List<CachedHotelImage>,
    val facilities: List<CachedHotelFacility>,
    val rooms: List<CachedHotelRoom>,
    val minRate: Double?,
    val maxRate: Double?,
    val currency: String?,

    // Search constraints used as composite primary key
    val searchCheckIn: String,
    val searchCheckOut: String,
    val searchAdults: Int,
    val searchChildren: Int,
    val searchChildrenAges: String,
    val timestamp: Long
)

@Serializable
data class CachedHotelPhone(
    val type: String?,
    val number: String?
)

@Serializable
data class CachedHotelFacility(
    val code: Int,
    val groupCode: Int,
    val description: String?
)

@Serializable
data class CachedHotelRoom(
    val code: String,
    val name: String,
    val images: List<CachedHotelImage>,
    val roomFacilities: List<String>,
    val rates: List<CachedRoomRate>
)

@Serializable
data class CachedRoomRate(
    val rateKey: String,
    val rateClass: String?,
    val price: Double?,
    val boardCode: String?,
    val boardName: String?,
    val allotment: Int?,
    val cancellationPolicies: List<CachedCancellationPolicy>
)

@Serializable
data class CachedCancellationPolicy(
    val amount: Double?,
    val from: String?
)
