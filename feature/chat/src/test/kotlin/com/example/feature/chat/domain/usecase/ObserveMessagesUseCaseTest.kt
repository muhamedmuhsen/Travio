package com.example.feature.chat.domain.usecase

import com.example.feature.chat.domain.model.ChatMessage
import com.example.feature.chat.domain.model.Sender
import com.example.feature.chat.domain.repository.FakeChatRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ObserveMessagesUseCaseTest {

    private lateinit var fakeRepository: FakeChatRepository
    private lateinit var observeMessagesUseCase: ObserveMessagesUseCase

    @Before
    fun setUp() {
        fakeRepository = FakeChatRepository()
        observeMessagesUseCase = ObserveMessagesUseCase(fakeRepository)
    }

    @Test
    fun `should observe messages from repository`() = runTest {
        // Given
        val threadId = "thread_123"
        val message = ChatMessage(threadId = threadId, sender = Sender.AI, content = "Response")

        // When
        val flow = observeMessagesUseCase(threadId)
        
        var receivedMessage: ChatMessage? = null
        val job = launch {
            receivedMessage = flow.first()
        }
        
        fakeRepository.emitMessage(message)
        
        job.join()

        // Then
        assertEquals(message, receivedMessage)
    }
}
