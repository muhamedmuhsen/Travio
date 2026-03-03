package com.example.domain.model.destination

data class Destination(
    val cityName: String,
    val description: String,
    val destinationID: Int,
    val imageUrls: List<String>,
    val interests: List<Interest>,
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val rating: Double,
    val totalReviews: Int
)
