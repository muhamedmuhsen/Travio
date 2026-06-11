package com.example.feature.chat.domain.model

data class PlanGenerationState(
    val threadId: String,
    val status: PlanStatus,
    val error: AiGenerationError? = null,
    val tripId: String? = null
)

enum class PlanStatus {
    IN_PROGRESS,
    COMPLETED,
    FAILED
}

sealed interface AiGenerationError {
    data object AiServiceUnavailable : AiGenerationError
    data object AiConnectionRefused : AiGenerationError
    data object AiTimeout : AiGenerationError
    data object AiProcessingFailed : AiGenerationError
    data object SyncTripIdFailed : AiGenerationError
    data class Unknown(val debugMessage: String) : AiGenerationError
}
