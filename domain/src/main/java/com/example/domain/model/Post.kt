package com.example.domain.model

data class Post(
    val id: Int,
    val title: String,
    val content: String,
    val createdAt: String,
    val postLikes: Int,
    val imageUrl: String,
    val author: String,
)
