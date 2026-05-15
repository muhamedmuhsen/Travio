package com.example.feature.chat.presentation.viewmodel

import com.example.feature.chat.domain.model.ChatMessage
import com.example.feature.chat.domain.model.Sender
import com.example.feature.chat.domain.repository.FakeChatRepository
import com.example.feature.chat.domain.usecase.ObserveMessagesUseCase
import com.example.feature.chat.domain.usecase.SendMessageUseCase
import com.example.feature.chat.domain.usecase.GetThreadHistoryUseCase
import com.example.feature.chat.domain.usecase.ObserveConnectionStateUseCase
import com.example.feature.chat.presentation.state.ChatUiState
import com.example.feature.chat.domain.model.ConnectionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {

    private lateinit var fakeRepository: FakeChatRepository
    private lateinit var observeMessagesUseCase: ObserveMessagesUseCase
    private lateinit var sendMessageUseCase: SendMessageUseCase
    private lateinit var viewModel: ChatViewModel
    
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeChatRepository()
        observeMessagesUseCase = ObserveMessagesUseCase(fakeRepository)
        sendMessageUseCase = SendMessageUseCase(fakeRepository)
        val getThreadHistoryUseCase = GetThreadHistoryUseCase(fakeRepository)
        val observeConnectionStateUseCase = ObserveConnectionStateUseCase(fakeRepository)
        viewModel = ChatViewModel(observeMessagesUseCase, sendMessageUseCase, getThreadHistoryUseCase, observeConnectionStateUseCase)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should initialize with Success state and empty messages`() = runTest {
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state is ChatUiState.Success)
        val successState = state as ChatUiState.Success
        assertTrue(successState.messages.isEmpty())
    }

    @Test
    fun `should update state when input text changes`() = runTest {
        advanceUntilIdle()

        viewModel.onInputTextChanged("Hello")

        val state = viewModel.state.value as ChatUiState.Success
        assertEquals("Hello", state.inputText)
    }

    @Test
    fun `should send message and clear input`() = runTest {
        advanceUntilIdle()
        viewModel.onInputTextChanged("Hello")

        viewModel.onSendMessage()
        advanceUntilIdle()

        val state = viewModel.state.value as ChatUiState.Success
        assertEquals("", state.inputText)
        assertEquals(1, fakeRepository.sentMessages.size)
        assertEquals("Hello", fakeRepository.sentMessages.first())
    }

    @Test
    fun `should not send message when disconnected`() = runTest {
        advanceUntilIdle()
        viewModel.onInputTextChanged("Hello")
        
        fakeRepository.setConnectionState(ConnectionState.DISCONNECTED)
        advanceUntilIdle()

        viewModel.onSendMessage()
        advanceUntilIdle()

        assertEquals(0, fakeRepository.sentMessages.size)
    }
}
