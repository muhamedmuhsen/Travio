package com.example.feature.chat.domain.usecase

import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import com.example.feature.chat.domain.model.ChatMessage
import com.example.feature.chat.domain.repository.ChatRepository
import javax.inject.Inject

class GetThreadHistoryUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(threadId: String): Result<List<ChatMessage>, DataError> {
        return repository.getThreadHistory(threadId)
    }
}
