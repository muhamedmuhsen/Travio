package com.example.data.mapper.trip

import com.example.domain.model.trip.FavoriteTripsPage
import com.example.domain.model.trip.TripActivity
import com.example.domain.model.trip.TripDay
import com.example.domain.model.trip.TripDetails
import com.example.domain.model.trip.TripHotel
import com.example.domain.model.trip.TripItem
import com.example.network.dto.trip.TripActivityDto
import com.example.network.dto.trip.TripDayDto
import com.example.network.dto.trip.TripDetailsDto
import com.example.network.dto.trip.TripDto
import com.example.network.dto.trip.TripHotelDto
import com.example.network.dto.trip.TripPageDto

fun TripPageDto.toDomain(): FavoriteTripsPage {
    return FavoriteTripsPage(
        pageIndex = pageIndex,
        pageSize = pageSize,
        count = count,
        data = data.map { it.toDomain() }
    )
}

fun TripDto.toDomain(): TripItem {
    return TripItem(
        id = id,
        title = title ?: "",
        destinationName = destinationName ?: "",
        totalDays = totalDays,
        isFavorite = isFavorite,
        createdAt = createdAt ?: ""
    )
}

fun TripDetailsDto.toDomain(): TripDetails {
    return TripDetails(
        id = id,
        title = title ?: "",
        destinationName = destinationName ?: "",
        totalDays = totalDays,
        isFavorite = isFavorite,
        createdAt = createdAt ?: "",
        days = days?.map { it.toDomain() } ?: emptyList(),
        hotels = hotels?.map { it.toDomain() } ?: emptyList()
    )
}

fun TripDayDto.toDomain(): TripDay {
    return TripDay(
        dayNumber = dayNumber,
        theme = theme ?: "",
        activities = activities?.map { it.toDomain() } ?: emptyList()
    )
}

fun TripActivityDto.toDomain(): TripActivity {
    return TripActivity(
        activityType = activityType ?: "",
        placeName = placeName ?: "",
        suggestedTime = suggestedTime ?: "",
        description = description ?: "",
        address = address ?: "",
        featuredImage = featuredImage ?: ""
    )
}

fun TripHotelDto.toDomain(): TripHotel {
    return TripHotel(
        name = name ?: "",
        description = description ?: "",
        rating = rating,
        address = address ?: "",
        link = link ?: "",
        featuredImage = featuredImage ?: ""
    )
}
