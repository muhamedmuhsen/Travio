package com.example.domain.model.trip

data class TripHotel(
    val name: String,
    val description: String,
    val rating: Int,
    val address: String,
    val link: String,
    val featuredImage: String,
    val latitude: Double?,
    val longitude: Double?
)
