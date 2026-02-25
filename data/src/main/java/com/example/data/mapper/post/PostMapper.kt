package com.example.data.mapper.post

import com.example.domain.model.Post

typealias DatabasePost = com.example.database.post.Post

fun Post.toEntity(): DatabasePost {
    return DatabasePost(
        id = id,
        title = title,
        content = content,
        createdAt = createdAt,
        postLikes = postLikes,
        imageUrl = imageUrl,
        author = author
    )
}

fun DatabasePost.toDomain(): Post {
    return Post(
        id = id,
        title = title,
        content = content,
        createdAt = createdAt,
        postLikes = postLikes,
        imageUrl = imageUrl,
        author = author
    )
}
