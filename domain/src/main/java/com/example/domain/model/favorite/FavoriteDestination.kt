package com.example.domain.model.favorite

data class FavoriteDestination(
    val destinationId: Int,
    val name: String,
    val description: String,
    val rating: Double,
    val cityName: String,
    val imageUrls: List<String>
) {
    val primaryImageUrl: String?
        get() = imageUrls.firstOrNull { it.isNotBlank() }
}
