package com.example.domain.model.destination

data class Destination(
    val cityName: String,
    val description: String,
    val destinationID: Int,
    val imageUrls: List<String>,
    val interests: List<Interest>,
    val latitude: Int,
    val longitude: Int,
    val name: String,
    val rating: Int,
    val totalReviews: Int
)
