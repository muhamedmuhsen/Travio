package com.example.feature.chat.domain.model

data class ChatThread(
    val threadId: String,
    val status: AiResponseStatus = AiResponseStatus.IDLE
)
