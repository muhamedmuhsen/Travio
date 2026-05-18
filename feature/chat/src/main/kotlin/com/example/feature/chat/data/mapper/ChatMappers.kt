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
        error = errorMessage,
        tripId = tripId,
        data = data
    )
}

fun com.example.network.dto.ai.AiStatusResponseDto.toFeatureDto(): com.example.feature.chat.data.remote.dto.AiStatusResponseDto {
    return com.example.feature.chat.data.remote.dto.AiStatusResponseDto(
        recommendedHotels = data?.recommendedHotels?.map { it.toFeatureDto() },
        itinerary = data?.itinerary?.map { it.toFeatureDto() }
    )
}

fun com.example.network.dto.ai.HotelDto.toFeatureDto(): com.example.feature.chat.data.remote.dto.HotelDto {
    return com.example.feature.chat.data.remote.dto.HotelDto(
        name = name,
        description = description,
        rating = rating,
        address = address,
        link = link,
        imageUrl = imageUrl
    )
}

fun com.example.network.dto.ai.TripDayDto.toFeatureDto(): com.example.feature.chat.data.remote.dto.TripDayDto {
    return com.example.feature.chat.data.remote.dto.TripDayDto(
        day = day,
        theme = theme,
        activities = activities?.map { it.toFeatureDto() }
    )
}

fun com.example.network.dto.ai.TripActivityDto.toFeatureDto(): com.example.feature.chat.data.remote.dto.TripActivityDto {
    return com.example.feature.chat.data.remote.dto.TripActivityDto(
        type = type,
        placeName = placeName,
        suggestedTime = suggestedTime,
        description = description,
        address = address,
        imageUrl = imageUrl
    )
}
