package com.example.domain.model.favorite

import com.example.domain.model.destination.Destination

fun Destination.toPlace(): Place =
    Place(
        id = destinationID,
        name = name,
        description = description,
        imageUrls = imageUrls,
        rating = rating
    )
