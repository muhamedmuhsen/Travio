package com.example.domain.model.trip

data class TripActivity(
    val activityType: String,
    val placeName: String,
    val suggestedTime: String,
    val description: String,
    val address: String,
    val featuredImage: String,
    val latitude: Double?,
    val longitude: Double?
)
