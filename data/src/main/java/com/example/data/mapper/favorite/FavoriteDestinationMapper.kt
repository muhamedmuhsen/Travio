package com.example.data.mapper.favorite

import com.example.data.BuildConfig
import com.example.domain.model.favorite.FavoriteDestination
import com.example.domain.model.favorite.FavoriteMutationResult
import com.example.domain.model.favorite.FavoritesPage
import com.example.network.dto.favorite.FavoriteDestinationDto
import com.example.network.dto.favorite.FavoritesPageDto
import com.example.network.dto.favorite.FavoritesResponseDto

private fun resolveImageUrl(path: String?): String {
    if (path.isNullOrBlank()) return ""
    if (path.startsWith("http", ignoreCase = true)) return path
    val base = BuildConfig.IMAGE_BASE_URL.trimEnd('/')
    val normalizedPath = if (path.startsWith('/')) path else "/$path"
    return base + normalizedPath
}

fun FavoriteDestinationDto.toDomain(): FavoriteDestination {
    return FavoriteDestination(
        destinationId = destinationId,
        name = name,
        description = description,
        rating = rating,
        cityName = cityName,
        imageUrls = imageUrls
            .map(::resolveImageUrl)
            .filter(String::isNotEmpty)
    )
}

fun FavoritesPageDto.toDomain(): FavoritesPage {
    return FavoritesPage(
        pageIndex = pageIndex,
        pageSize = pageSize,
        count = count,
        items = items.map { it.toDomain() }
    )
}

fun FavoritesResponseDto<Boolean>.toMutationResult(): FavoriteMutationResult {
    return FavoriteMutationResult(
        isSuccess = success,
        message = message,
        errors = errors
    )
}
