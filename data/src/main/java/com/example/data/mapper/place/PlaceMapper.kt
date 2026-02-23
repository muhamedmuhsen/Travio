package com.example.data.mapper.place

import com.example.domain.model.Place

typealias DatabasePlace = com.example.database.place.Place

fun Place.toEntity(): DatabasePlace {
    return DatabasePlace(
        id = id,
        name = name,
        description = description,
        imageUrl = imageUrl,
        rating = rating
    )
}

fun DatabasePlace.toDomain(): Place {
    return Place(
        id = id,
        name = name,
        description = description,
        imageUrl = imageUrl,
        rating = rating
    )
}