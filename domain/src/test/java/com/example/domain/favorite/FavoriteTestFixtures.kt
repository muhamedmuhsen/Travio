package com.example.domain.favorite

import com.example.domain.model.favorite.FavoriteDestination
import com.example.domain.model.favorite.FavoriteMutationResult
import com.example.domain.model.favorite.FavoritesPage

object FavoriteTestFixtures {
    fun destination(
        destinationId: Int = 1,
        name: String = "Cappadocia",
        description: String = "Hot air balloons and valleys",
        rating: Double = 4.8,
        cityName: String = "Nevsehir",
        imageUrls: List<String> = listOf("https://example.com/cappadocia.jpg")
    ): FavoriteDestination {
        return FavoriteDestination(
            destinationId = destinationId,
            name = name,
            description = description,
            rating = rating,
            cityName = cityName,
            imageUrls = imageUrls
        )
    }

    fun page(
        pageIndex: Int = 1,
        pageSize: Int = 10,
        count: Int = 1,
        items: List<FavoriteDestination> = listOf(destination())
    ): FavoritesPage {
        return FavoritesPage(
            pageIndex = pageIndex,
            pageSize = pageSize,
            count = count,
            items = items
        )
    }

    fun mutation(
        isSuccess: Boolean = true,
        message: String? = null,
        errors: List<String> = emptyList()
    ): FavoriteMutationResult {
        return FavoriteMutationResult(
            isSuccess = isSuccess,
            message = message,
            errors = errors
        )
    }
}

