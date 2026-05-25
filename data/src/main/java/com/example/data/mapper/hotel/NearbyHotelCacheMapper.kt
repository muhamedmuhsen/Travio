package com.example.data.mapper.hotel

import com.example.database.hotel.CachedHotelImage
import com.example.database.hotel.CachedNearbyHotel
import com.example.domain.model.hotel.HotelImage
import com.example.domain.model.hotel.NearbyHotel

fun CachedNearbyHotel.toDomain(): NearbyHotel {
    return NearbyHotel(
        code = code,
        name = name,
        categoryName = categoryName,
        destinationName = destinationName,
        latitude = latitude,
        longitude = longitude,
        minRate = minRate,
        maxRate = maxRate,
        currency = currency,
        thumbnailImage = thumbnailImage,
        images = images.map { it.toDomain() }
    )
}

fun CachedHotelImage.toDomain(): HotelImage {
    return HotelImage(
        url = url,
        type = type,
        order = order
    )
}

fun NearbyHotel.toCached(
    searchLatitude: Double,
    searchLongitude: Double,
    checkIn: String,
    checkOut: String,
    timestamp: Long
): CachedNearbyHotel {
    return CachedNearbyHotel(
        code = code,
        name = name,
        categoryName = categoryName,
        destinationName = destinationName,
        latitude = latitude,
        longitude = longitude,
        minRate = minRate,
        maxRate = maxRate,
        currency = currency,
        thumbnailImage = thumbnailImage,
        images = images.map { it.toCached() },
        searchLatitude = searchLatitude,
        searchLongitude = searchLongitude,
        checkIn = checkIn,
        checkOut = checkOut,
        timestamp = timestamp
    )
}

fun HotelImage.toCached(): CachedHotelImage {
    return CachedHotelImage(
        url = url,
        type = type,
        order = order
    )
}
