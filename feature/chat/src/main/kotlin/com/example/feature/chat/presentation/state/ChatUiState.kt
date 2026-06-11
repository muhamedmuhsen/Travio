package com.example.feature.chat.presentation.state

import com.example.feature.chat.domain.model.ChatMessage
import com.example.feature.chat.domain.model.ConnectionState

enum class GenerationStatus {
    IDLE,
    PROCESSING,
    COMPLETED,
    FAILED
}

sealed interface ChatUiState {
    data object Loading : ChatUiState

    data class Success(
        val messages: List<ChatMessage> = emptyList(),
        val inputText: String = "",
        val connectionState: ConnectionState = ConnectionState.CONNECTED,
        val isSending: Boolean = false,
        val isAiThinking: Boolean = false,
        val isGeneratingPlan: Boolean = false,
        val generatedTripId: String? = null,
        val generationStatus: GenerationStatus = GenerationStatus.IDLE,
        val error: com.example.feature.chat.domain.model.AiGenerationError? = null
    ) : ChatUiState

    data class Error(val message: String) : ChatUiState
}
