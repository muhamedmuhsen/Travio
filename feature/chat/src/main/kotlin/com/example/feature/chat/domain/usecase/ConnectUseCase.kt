package com.example.feature.chat.domain.usecase

import com.example.feature.chat.domain.repository.ChatRepository
import javax.inject.Inject

class ConnectUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    suspend operator fun invoke() {
        repository.connect()
    }
}
