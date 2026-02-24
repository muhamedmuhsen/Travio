package com.example.network.dto.destinations

data class GetAllDestinationsRequest(
    val pageIndex: Int,
    val pageSize: Int,
    val cityId: Int,
    val interestId: Int,
    val sortBy: Int = 0
)
