package com.example.domain.model.hotel

data class NearbyHotel(
    val code: Int,
    val name: String,
    val categoryName: String?,
    val destinationName: String?,
    val latitude: Double?,
    val longitude: Double?,
    val minRate: Double?,
    val maxRate: Double?,
    val currency: String?,
    val thumbnailImage: String?,
    val images: List<HotelImage>
)
