package com.example.feature.chat.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PlanStatusDto(
    val threadId: String,
    val isCompleted: Boolean,
    val isFailed: Boolean,
    val errorMessage: String? = null,
    val data: AiStatusResponseDto? = null,
    val tripId: String? = null
)
