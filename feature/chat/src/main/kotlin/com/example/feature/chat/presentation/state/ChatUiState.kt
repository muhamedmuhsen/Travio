package com.example.feature.chat.presentation.state

import com.example.feature.chat.domain.model.ChatMessage
import com.example.feature.chat.domain.model.ConnectionState

sealed interface ChatUiState {
    data object Loading : ChatUiState

    data class Success(
        val messages: List<ChatMessage> = emptyList(),
        val inputText: String = "",
        val connectionState: ConnectionState = ConnectionState.CONNECTED,
        val isSending: Boolean = false
    ) : ChatUiState

    data class Error(val message: String) : ChatUiState
}
