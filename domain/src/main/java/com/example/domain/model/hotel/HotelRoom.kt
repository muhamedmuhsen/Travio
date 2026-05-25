package com.example.domain.model.hotel

data class HotelRoom(
    val code: String,
    val name: String,
    val images: List<HotelImage>,
    val roomFacilities: List<String>,
    val rates: List<RoomRate>
)
