package com.example.feature.chat.data.remote

import com.example.feature.chat.data.remote.dto.AiResponseDto
import com.example.feature.chat.data.remote.dto.PlanStatusDto
import com.example.feature.chat.domain.model.ConnectionState
import kotlinx.coroutines.flow.Flow

interface SignalRService {
    fun observeMessages(): Flow<AiResponseDto>
    fun observeConnectionState(): Flow<ConnectionState>
    fun observePlanStatus(): Flow<PlanStatusDto>
    fun observeStatus(): Flow<String>
    suspend fun sendMessage(
        threadId: String,
        content: String
    )
    suspend fun connect()
    suspend fun disconnect()
}
