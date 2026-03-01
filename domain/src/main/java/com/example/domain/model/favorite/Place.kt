package com.example.domain.model.favorite

data class Place(
    val id: Int = 0,
    val name: String,
    val description: String,
    val imageUrls: List<String>
)
