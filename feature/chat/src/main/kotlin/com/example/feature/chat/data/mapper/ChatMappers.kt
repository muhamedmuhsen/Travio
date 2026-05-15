package com.example.feature.chat.data.mapper

import com.example.feature.chat.data.remote.dto.AiResponseDto
import com.example.feature.chat.data.remote.dto.PlanStatusDto
import com.example.feature.chat.domain.model.AiResponseStatus
import com.example.feature.chat.domain.model.ChatMessage
import com.example.feature.chat.domain.model.MessageStatus
import com.example.feature.chat.domain.model.PlanGenerationState
import com.example.feature.chat.domain.model.PlanStatus
import com.example.feature.chat.domain.model.Sender

fun AiResponseDto.toChatMessage(): ChatMessage {
    return ChatMessage(
        threadId = threadId,
        sender = Sender.AI,
        content = messageChunk,
        status = MessageStatus.SENT
    )
}

fun String.toAiResponseStatus(): AiResponseStatus {
    return try {
        AiResponseStatus.valueOf(this.uppercase())
    } catch (e: IllegalArgumentException) {
        AiResponseStatus.IDLE
    }
}

fun PlanStatusDto.toPlanGenerationState(): PlanGenerationState {
    val status = when {
        isCompleted -> PlanStatus.COMPLETED
        isFailed -> PlanStatus.FAILED
        else -> PlanStatus.IN_PROGRESS
    }
    return PlanGenerationState(
        threadId = threadId,
        status = status,
        error = errorMessage
    )
}
