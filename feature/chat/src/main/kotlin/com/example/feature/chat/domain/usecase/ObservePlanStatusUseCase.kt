package com.example.feature.chat.domain.usecase

import com.example.feature.chat.domain.model.PlanGenerationState
import com.example.feature.chat.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePlanStatusUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    operator fun invoke(threadId: String): Flow<PlanGenerationState> {
        return repository.observePlanStatus(threadId)
    }
}
