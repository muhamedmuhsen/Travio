package com.example.feature.chat.domain.model

data class PlanGenerationState(
    val threadId: String,
    val status: PlanStatus,
    val error: String? = null
)

enum class PlanStatus {
    IN_PROGRESS,
    COMPLETED,
    FAILED
}
