package com.dev.favroite

import com.example.domain.model.favorite.Place
import com.example.domain.model.favorite.Post

object FavoriteTestFixtures {
    fun place(
        id: Int = 1,
        name: String = "Cappadocia",
        description: String = "Nevsehir, Turkiye",
        imageUrls: List<String> = listOf("https://example.com/place.jpg")
    ): Place = Place(
        id = id,
        name = name,
        description = description,
        imageUrls = imageUrls
    )

    fun trip(
        id: Int = 1,
        title: String = "Weekend Escape",
        createdAt: String = "2026-04-11T10:00:00Z",
        imageUrl: String = "https://example.com/trip.jpg",
        author: String = "Traveler"
    ): Post = Post(
        id = id,
        title = title,
        content = "",
        createdAt = createdAt,
        postLikes = 0,
        imageUrl = imageUrl,
        author = author
    )
}

