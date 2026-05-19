package com.example.feature.chat.domain.usecase

import com.example.feature.chat.domain.repository.FakeChatRepository
import com.example.domain.utils.Result
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SendMessageUseCaseTest {

    private lateinit var fakeRepository: FakeChatRepository
    private lateinit var sendMessageUseCase: SendMessageUseCase

    @Before
    fun setUp() {
        fakeRepository = FakeChatRepository()
        sendMessageUseCase = SendMessageUseCase(fakeRepository)
    }

    @Test
    fun `should call repository sendMessage when invoked`() = runTest {
        // Given
        val threadId = "thread_123"
        val content = "Hello"

        // When
        val result = sendMessageUseCase(threadId, content)

        org.junit.Assert.assertTrue(result is Result.Success)
        assertEquals(1, fakeRepository.sentMessages.size)
        assertEquals("Hello", fakeRepository.sentMessages.first())
    }
}
