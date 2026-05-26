package com.example.domain.model.trip

data class TripDay(
    val dayNumber: Int,
    val theme: String,
    val activities: List<TripActivity>
)
