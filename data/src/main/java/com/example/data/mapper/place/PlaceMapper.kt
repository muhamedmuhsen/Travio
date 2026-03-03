package com.example.data.mapper.place

import com.example.domain.model.destination.Destination
import com.example.domain.model.favorite.Place

typealias DatabasePlace = com.example.database.place.Place

fun Place.toEntity(): DatabasePlace {
    return DatabasePlace(
        id = id,
        name = name,
        description = description,
        imageUrls = imageUrls
    )
}

fun DatabasePlace.toDomain(): Place {
    return Place(
        id = id,
        name = name,
        description = description,
        imageUrls = imageUrls
    )
}

fun Destination.toPlace(): Place {
    return Place(
        id = destinationID,
        name = name,
        description = description,
        imageUrls = imageUrls
    )
}
