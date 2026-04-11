package com.example.data.mapper.trip

import com.example.domain.model.favorite.Trip

typealias DatabasePost = com.example.database.post.Post

fun Trip.toEntity(): DatabasePost {
    return DatabasePost(
        id = id,
        title = title,
        content = "",
        createdAt = savedAt,
        postLikes = 0,
        imageUrl = imageUrl,
        author = ownerName
    )
}

fun DatabasePost.toTripDomain(): Trip {
    return Trip(
        id = id,
        title = title,
        ownerName = author,
        imageUrl = imageUrl,
        savedAt = createdAt
    )
}
