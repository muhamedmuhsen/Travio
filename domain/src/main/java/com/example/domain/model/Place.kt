package com.example.domain.model

data class Place(
    val id: Int = 0,
    val name: String,
    val description: String,
    val imageUrl: String,
    val rating: Float,
)
