package com.example.data.mapper.destination

import com.example.network.dto.destinations.Country
import com.example.network.dto.destinations.Destination
import com.example.network.dto.destinations.Interest

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
        flagURL = this.flagURL,
        name = this.name
    )
}
