package com.example.domain.model.trip

data class FavoriteTripsPage(
    val pageIndex: Int,
    val pageSize: Int,
    val count: Int,
    val data: List<TripItem>
)
