package com.example.feature.chat.domain.model

data class PlanGenerationState(
    val threadId: String,
    val status: PlanStatus,
    val error: String? = null,
    val tripId: String? = null,
    val data: com.example.feature.chat.data.remote.dto.AiStatusResponseDto? = null
)

enum class PlanStatus {
    IN_PROGRESS,
    COMPLETED,
    FAILED
}
