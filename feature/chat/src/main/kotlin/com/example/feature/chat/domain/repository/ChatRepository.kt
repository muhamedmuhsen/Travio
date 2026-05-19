package com.example.feature.chat.domain.repository

import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import com.example.feature.chat.domain.model.ChatMessage
import com.example.feature.chat.domain.model.ConnectionState
import com.example.feature.chat.domain.model.PlanGenerationState
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun observeMessages(threadId: String): Flow<ChatMessage>
    suspend fun sendMessage(
        threadId: String,
        content: String
    ): Result<Unit, DataError>
    fun observeConnectionState(): Flow<ConnectionState>
    fun observePlanStatus(threadId: String): Flow<PlanGenerationState>
    fun observeStatus(): Flow<String>
    suspend fun getThreadHistory(threadId: String): Result<List<ChatMessage>, DataError>
    suspend fun connect()
}
