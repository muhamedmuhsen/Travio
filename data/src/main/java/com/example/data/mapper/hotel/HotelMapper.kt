package com.example.data.mapper.hotel

import com.example.data.BuildConfig
import com.example.domain.model.hotel.HotelImage
import com.example.domain.model.hotel.NearbyHotel
import com.example.network.dto.hotel.HotelDto
import com.example.network.dto.hotel.HotelImageDto

private fun resolveImageUrl(path: String?): String? {
    if (path.isNullOrBlank()) return null
    if (path.startsWith("http", ignoreCase = true)) return path
    val base = BuildConfig.IMAGE_BASE_URL.trimEnd('/')
    val normalizedPath = if (path.startsWith('/')) path else "/$path"
    return base + normalizedPath
}

fun HotelImageDto.toDomain(): HotelImage? {
    val urlString = resolveImageUrl(this.url) ?: return null
    return HotelImage(
        url = urlString,
        type = this.type,
        order = this.order
    )
}

fun HotelDto.toDomain(): NearbyHotel {
    return NearbyHotel(
        code = this.code,
        name = this.name ?: "Unknown Hotel",
        categoryName = this.categoryName,
        destinationName = this.destinationName,
        latitude = this.latitude,
        longitude = this.longitude,
        minRate = this.minRate,
        maxRate = this.maxRate,
        currency = this.currency,
        thumbnailImage = resolveImageUrl(this.thumbnailImage),
        images = this.images?.mapNotNull { it.toDomain() } ?: emptyList()
    )
}
