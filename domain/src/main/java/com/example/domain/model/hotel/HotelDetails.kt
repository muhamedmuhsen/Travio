package com.example.domain.model.hotel

data class HotelDetails(
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
    val phones: List<HotelPhone>,
    val images: List<HotelImage>,
    val facilities: List<HotelFacility>,
    val rooms: List<HotelRoom>,
    val minRate: Double?,
    val maxRate: Double?,
    val currency: String?
)
