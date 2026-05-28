package com.example.feature.chat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.utils.Result
import com.example.feature.chat.domain.model.ConnectionState
import com.example.feature.chat.domain.model.PlanStatus
import com.example.feature.chat.domain.model.Sender
import com.example.feature.chat.domain.usecase.ConnectUseCase
import com.example.feature.chat.domain.usecase.GetThreadHistoryUseCase
import com.example.feature.chat.domain.usecase.ObserveAiStatusUseCase
import com.example.feature.chat.domain.usecase.ObserveConnectionStateUseCase
import com.example.feature.chat.domain.usecase.ObserveMessagesUseCase
import com.example.feature.chat.domain.usecase.ObservePlanStatusUseCase
import com.example.feature.chat.domain.usecase.SendMessageUseCase
import com.example.feature.chat.presentation.state.ChatUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val observeMessagesUseCase: ObserveMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val getThreadHistoryUseCase: GetThreadHistoryUseCase,
    private val observeConnectionStateUseCase: ObserveConnectionStateUseCase,
    private val observePlanStatusUseCase: ObservePlanStatusUseCase,
    private val connectUseCase: ConnectUseCase,
    private val observeAiStatusUseCase: ObserveAiStatusUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<ChatUiState>(ChatUiState.Loading)
    val state = _state.asStateFlow()

    private val _navigationEvent = Channel<ChatNavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    private val _snackbarEvent = Channel<String>()
    val snackbarEvent = _snackbarEvent.receiveAsFlow()

    private val currentThreadId: String = java.util.UUID.randomUUID().toString()

    init {
        loadMessages()
        observeConnection()
        observePlanStatus()
        observeAiStatus()
        connect()
    }

    private fun connect() {
        viewModelScope.launch {
            connectUseCase()
        }
    }

    private fun loadMessages() {
        viewModelScope.launch {
            val historyResult = getThreadHistoryUseCase(currentThreadId)
            val history = if (historyResult is Result.Success) historyResult.data else emptyList()

            _state.value = ChatUiState.Success(messages = history)

            observeMessagesUseCase(currentThreadId).collect { message ->
                if (message.threadId != currentThreadId) return@collect
                _state.update { currentState ->
                    if (currentState is ChatUiState.Success) {
                        val messages = currentState.messages
                        if (messages.any { it.id == message.id }) {
                            return@update currentState
                        }
                        val updatedMessages = if (messages.isNotEmpty() && messages.last().sender == Sender.AI) {
                            // Append chunk to last message
                            val last = messages.last()
                            messages.dropLast(1) + last.copy(content = last.content + message.content)
                        } else {
                            // Add new message (first chunk of a new AI response)
                            messages + message
                        }
                        currentState.copy(messages = updatedMessages)
                    } else {
                        currentState
                    }
                }
            }
        }
    }

    private fun observeConnection() {
        viewModelScope.launch {
            observeConnectionStateUseCase().collect { connectionState ->
                _state.update { currentState ->
                    if (currentState is ChatUiState.Success) {
                        currentState.copy(connectionState = connectionState)
                    } else {
                        currentState
                    }
                }
            }
        }
    }

    private fun observePlanStatus() {
        viewModelScope.launch {
            observePlanStatusUseCase(currentThreadId).collect { planState ->
                _state.update { currentState ->
                    if (currentState is ChatUiState.Success) {
                        val genStatus = when (planState.status) {
                            PlanStatus.IN_PROGRESS -> com.example.feature.chat.presentation.state.GenerationStatus.PROCESSING
                            PlanStatus.COMPLETED -> com.example.feature.chat.presentation.state.GenerationStatus.COMPLETED
                            PlanStatus.FAILED -> com.example.feature.chat.presentation.state.GenerationStatus.FAILED
                        }
                        currentState.copy(
                            isGeneratingPlan = planState.status == PlanStatus.IN_PROGRESS,
                            generatedTripId = planState.tripId,
                            generationStatus = genStatus
                        )
                    } else {
                        currentState
                    }
                }
                if (planState.status == PlanStatus.IN_PROGRESS) {
                    _navigationEvent.send(ChatNavigationEvent.NavigateToPlanGeneration(currentThreadId))
                }
            }
        }
    }

    private fun observeAiStatus() {
        viewModelScope.launch {
            observeAiStatusUseCase().collect { status ->
                _state.update { currentState ->
                    if (currentState is ChatUiState.Success) {
                        currentState.copy(isAiThinking = status == "thinking")
                    } else {
                        currentState
                    }
                }
            }
        }
    }

    fun onInputTextChanged(text: String) {
        _state.update { currentState ->
            if (currentState is ChatUiState.Success) {
                currentState.copy(inputText = text)
            } else {
                currentState
            }
        }
    }

    fun onSendMessage() {
        val currentState = _state.value
        if (currentState is ChatUiState.Success &&
            currentState.inputText.isNotBlank() &&
            currentState.connectionState == ConnectionState.CONNECTED
        ) {
            val content = currentState.inputText

            // Add user message to list immediately
            val userMessage = com.example.feature.chat.domain.model.ChatMessage(
                threadId = currentThreadId,
                sender = com.example.feature.chat.domain.model.Sender.USER,
                content = content
            )

            _state.update {
                if (it is ChatUiState.Success) {
                    it.copy(
                        inputText = "",
                        isSending = true,
                        messages = it.messages + userMessage
                    )
                } else {
                    it
                }
            }

            viewModelScope.launch {
                val result = sendMessageUseCase(currentThreadId, content)
                if (result is Result.Error) {
                    _snackbarEvent.send("Failed to send message")
                }
                _state.update { currentState ->
                    if (currentState is ChatUiState.Success) {
                        currentState.copy(isSending = false)
                    } else {
                        currentState
                    }
                }
            }
        }
    }
}

sealed interface ChatNavigationEvent {
    data class NavigateToPlanGeneration(val threadId: String) : ChatNavigationEvent
}
