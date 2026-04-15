package com.dev.favroite

import com.dev.utils.uitext.UiText
import com.example.domain.model.favorite.Place

object FavoritePresentationFixtures {
    fun destinationPlace(
        id: Int = 1,
        name: String = "Cappadocia",
        description: String = "Nevsehir, Turkiye",
        imageUrls: List<String> = listOf("https://example.com/cappadocia.jpg")
    ): Place {
        return Place(
            id = id,
            name = name,
            description = description,
            imageUrls = imageUrls
        )
    }

    fun loadingState(): FavoriteState = FavoriteState(
        destinationsState = FavoritesTabUiState.Loading
    )

    fun successState(items: List<Place> = listOf(destinationPlace())): FavoriteState = FavoriteState(
        loadedDestinations = items,
        destinationsState = FavoritesTabUiState.Success(items)
    )

    fun emptyState(): FavoriteState = FavoriteState(
        loadedDestinations = emptyList(),
        destinationsState = FavoritesTabUiState.Empty
    )

    fun errorState(message: String = "Unable to load favorites"): FavoriteState = FavoriteState(
        loadedDestinations = emptyList(),
        destinationsState = FavoritesTabUiState.Error(UiText.DynamicString(message))
    )
}

