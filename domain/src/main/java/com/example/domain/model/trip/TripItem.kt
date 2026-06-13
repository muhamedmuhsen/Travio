package com.example.domain.model.trip

data class TripItem(
    val id: Int,
    val title: String,
    val destinationName: String,
    val cityHeroImage: String,
    val totalDays: Int,
    val isFavorite: Boolean,
    val createdAt: String
)
