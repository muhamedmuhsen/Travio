package com.example.data.favorite

import com.example.network.dto.favorite.FavoriteDestinationDto
import com.example.network.dto.favorite.FavoritesPageDto
import com.example.network.dto.favorite.FavoritesResponseDto

object FavoriteTestFixtures {
    fun destinationDto(
        destinationId: Int = 1,
        name: String = "Cappadocia",
        description: String = "Hot air balloons and valleys",
        rating: Double = 4.8,
        cityName: String = "Nevsehir",
        imageUrls: List<String> = listOf("https://example.com/cappadocia.jpg")
    ): FavoriteDestinationDto {
        return FavoriteDestinationDto(
            destinationId = destinationId,
            name = name,
            description = description,
            rating = rating,
            cityName = cityName,
            imageUrls = imageUrls
        )
    }

    fun pageDto(
        pageIndex: Int = 1,
        pageSize: Int = 10,
        count: Int = 1,
        items: List<FavoriteDestinationDto> = listOf(destinationDto())
    ): FavoritesPageDto {
        return FavoritesPageDto(
            pageIndex = pageIndex,
            pageSize = pageSize,
            count = count,
            items = items
        )
    }

    fun pageEnvelope(
        success: Boolean = true,
        message: String? = null,
        errors: List<String> = emptyList(),
        data: FavoritesPageDto? = pageDto()
    ): FavoritesResponseDto<FavoritesPageDto> {
        return FavoritesResponseDto(
            data = data,
            success = success,
            message = message,
            errors = errors
        )
    }

    fun mutationEnvelope(
        success: Boolean = true,
        message: String? = null,
        errors: List<String> = emptyList(),
        data: Boolean? = true
    ): FavoritesResponseDto<Boolean> {
        return FavoritesResponseDto(
            data = data,
            success = success,
            message = message,
            errors = errors
        )
    }
}

