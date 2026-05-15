package com.example.feature.chat.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AiResponseDto(
    val threadId: String,
    val messageChunk: String,
    val status: String
)
