package com.example.domain.model.community

data class Comment(
    val id: Int,
    val authorName: String,
    val avatarUrl: String = "",
    val text: String,
    val timeAgo: String
)
