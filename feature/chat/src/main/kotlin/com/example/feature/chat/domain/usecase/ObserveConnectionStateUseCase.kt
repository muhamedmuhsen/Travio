package com.example.feature.chat.domain.usecase

import com.example.feature.chat.domain.model.ConnectionState
import com.example.feature.chat.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveConnectionStateUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    operator fun invoke(): Flow<ConnectionState> {
        return repository.observeConnectionState()
    }
}
