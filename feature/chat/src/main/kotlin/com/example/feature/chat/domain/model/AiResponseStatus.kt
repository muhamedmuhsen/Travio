package com.example.feature.chat.domain.model

enum class AiResponseStatus {
    IDLE,
    PROCESSING,
    STREAMING,
    COMPLETED,
    FAILED
}
