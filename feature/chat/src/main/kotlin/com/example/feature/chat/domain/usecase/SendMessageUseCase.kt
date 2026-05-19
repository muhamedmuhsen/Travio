package com.example.feature.chat.domain.usecase

import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import com.example.feature.chat.domain.repository.ChatRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(
        threadId: String,
        content: String
    ): Result<Unit, DataError> {
        return repository.sendMessage(threadId, content)
    }
}
