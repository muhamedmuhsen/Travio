package com.example.database.post

import androidx.room.Embedded
import androidx.room.Relation

data class PostWithComments(
    @Embedded val post: Post,
    @Relation(
        parentColumn = "id",
        entityColumn = "postId"
    )
    val comments: List<Comment>
)
