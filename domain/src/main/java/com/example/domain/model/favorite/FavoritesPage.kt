package com.example.domain.model.favorite

data class FavoritesPage(
    val pageIndex: Int,
    val pageSize: Int,
    val count: Int,
    val items: List<FavoriteDestination>
)
