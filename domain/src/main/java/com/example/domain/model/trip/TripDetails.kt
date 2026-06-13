package com.example.domain.model.trip

data class TripDetails(
    val id: Int,
    val title: String,
    val destinationName: String,
    val cityHeroImage: String,
    val totalDays: Int,
    val isFavorite: Boolean,
    val createdAt: String,
    val days: List<TripDay>,
    val hotels: List<TripHotel>
)
