package com.example.data.mapper.destination

import com.example.data.BuildConfig
import com.example.domain.model.favorite.Place
import com.example.network.dto.destinations.Country
import com.example.network.dto.destinations.Destination
import com.example.network.dto.destinations.Interest

private fun resolveCountryImageUrl(path: String?): String {
    if (path.isNullOrBlank()) return ""
    if (path.startsWith("http", ignoreCase = true)) return path
    val base = BuildConfig.IMAGE_BASE_URL.trimEnd('/')
    val normalizedPath = if (path.startsWith('/')) path else "/$path"
    return base + normalizedPath
}

fun Destination.toDomain(): com.example.domain.model.destination.Destination {
    return com.example.domain.model.destination.Destination(
        cityName = this.cityName,
        description = this.description,
        destinationID = this.destinationID,
        imageUrls = this.imageUrls,
        interests = this.interests.map { it.toDomain() },
        latitude = this.latitude,
        longitude = this.longitude,
        name = this.name,
        rating = this.rating,
        totalReviews = this.totalReviews
    )
}

fun Interest.toDomain(): com.example.domain.model.destination.Interest {
    return com.example.domain.model.destination.Interest(
        interestID = this.interestID,
        interestName = this.interestName
    )
}

fun Country.toDomain(): com.example.domain.model.destination.Country {
    return com.example.domain.model.destination.Country(
        countryID = this.countryID,
        flagURL = resolveCountryImageUrl(this.imageURL),
        name = this.name
    )
}

fun com.example.domain.model.destination.Destination.toEntity(): Place {
    return Place(
        name = name,
        description = description,
        imageUrls = imageUrls
    )
}
