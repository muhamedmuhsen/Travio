package com.example.feature.chat.domain.model

import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val threadId: String,
    val sender: Sender,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: MessageStatus = MessageStatus.SENT
)

enum class Sender {
    USER,
    AI
}

enum class MessageStatus {
    SENDING,
    SENT,
    FAILED
}
