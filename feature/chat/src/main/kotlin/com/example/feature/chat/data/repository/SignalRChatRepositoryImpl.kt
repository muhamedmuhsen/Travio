package com.example.feature.chat.data.repository

import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import com.example.feature.chat.data.mapper.toChatMessage
import com.example.feature.chat.data.mapper.toPlanGenerationState
import com.example.feature.chat.data.remote.SignalRService
import com.example.feature.chat.domain.model.ChatMessage
import com.example.feature.chat.domain.model.ConnectionState
import com.example.feature.chat.domain.model.PlanGenerationState
import com.example.feature.chat.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SignalRChatRepositoryImpl @Inject constructor(
    private val signalRService: SignalRService
) : ChatRepository {

    override fun observeMessages(threadId: String): Flow<ChatMessage> {
        return signalRService.observeMessages()
            .map { it.toChatMessage() }
    }

    override suspend fun sendMessage(
        threadId: String,
        content: String
    ): Result<Unit, DataError> {
        return try {
            signalRService.sendMessage(threadId, content)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DataError.UnknownError)
        }
    }

    override fun observeConnectionState(): Flow<ConnectionState> {
        return signalRService.observeConnectionState()
    }

    override fun observePlanStatus(threadId: String): Flow<PlanGenerationState> {
        return signalRService.observePlanStatus()
            .map { it.toPlanGenerationState() }
    }

    override suspend fun getThreadHistory(threadId: String): Result<List<ChatMessage>, DataError> {
        return Result.Success(emptyList())
    }

    override suspend fun connect() {
        signalRService.connect()
    }
}
