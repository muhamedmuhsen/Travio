package com.example.feature.chat.domain.repository

import com.example.feature.chat.domain.model.ChatMessage
import com.example.feature.chat.domain.model.ConnectionState
import com.example.feature.chat.domain.model.PlanGenerationState
import com.example.domain.utils.Result
import com.example.domain.utils.DataError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeChatRepository : ChatRepository {

    private val _messages = MutableSharedFlow<ChatMessage>()
    private val _connectionState = MutableStateFlow(ConnectionState.CONNECTED)
    private val _planStatus = MutableSharedFlow<PlanGenerationState>()
    private val _status = MutableSharedFlow<String>()

    var sendMessageResult: Result<Unit, DataError> = Result.Success(Unit)
    var getThreadHistoryResult: Result<List<ChatMessage>, DataError> = Result.Success(emptyList())

    val sentMessages = mutableListOf<String>()

    override fun observeMessages(threadId: String): Flow<ChatMessage> {
        return _messages.asSharedFlow()
    }

    override suspend fun sendMessage(threadId: String, content: String): Result<Unit, DataError> {
        sentMessages.add(content)
        return sendMessageResult
    }

    override fun observeConnectionState(): Flow<ConnectionState> {
        return _connectionState.asStateFlow()
    }

    override fun observePlanStatus(threadId: String): Flow<PlanGenerationState> {
        return _planStatus.asSharedFlow()
    }

    override fun observeStatus(): Flow<String> {
        return _status.asSharedFlow()
    }

    override suspend fun getThreadHistory(threadId: String): Result<List<ChatMessage>, DataError> {
        return getThreadHistoryResult
    }

    override suspend fun connect() {
        // No-op for fake
    }

    // Helper methods for testing
    suspend fun emitMessage(message: ChatMessage) {
        _messages.emit(message)
    }

    fun setConnectionState(state: ConnectionState) {
        _connectionState.value = state
    }

    suspend fun emitPlanStatus(state: PlanGenerationState) {
        _planStatus.emit(state)
    }

    suspend fun emitStatus(status: String) {
        _status.emit(status)
    }
}
