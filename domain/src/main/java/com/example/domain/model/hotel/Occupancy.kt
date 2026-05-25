package com.example.domain.model.hotel

data class Occupancy(
    val adults: Int,
    val children: Int,
    val childrenAges: List<Int>
)
