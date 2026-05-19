package com.example.feature.chat.domain.usecase

import com.example.feature.chat.domain.model.ChatMessage
import com.example.feature.chat.domain.model.Sender
import com.example.feature.chat.domain.repository.FakeChatRepository
import com.example.domain.utils.Result
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetThreadHistoryUseCaseTest {

    private lateinit var fakeRepository: FakeChatRepository
    private lateinit var getThreadHistoryUseCase: GetThreadHistoryUseCase

    @Before
    fun setUp() {
        fakeRepository = FakeChatRepository()
        getThreadHistoryUseCase = GetThreadHistoryUseCase(fakeRepository)
    }

    @Test
    fun `should return history from repository`() = runTest {
        val threadId = "thread_123"
        val history = listOf(
            ChatMessage(threadId = threadId, sender = Sender.USER, content = "Hi"),
            ChatMessage(threadId = threadId, sender = Sender.AI, content = "Hello")
        )
        fakeRepository.getThreadHistoryResult = Result.Success(history)

        val result = getThreadHistoryUseCase(threadId)

        org.junit.Assert.assertTrue(result is Result.Success)
        assertEquals(history, (result as Result.Success).data)
    }
}
