package com.example.domain.usecase.auth.session

import com.example.domain.repository.auth.SessionRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import javax.inject.Inject

class LogoutUseCase @Inject constructor(private val repository: SessionRepository) {
    suspend operator fun invoke(): Result<Unit, DataError> {
        return repository.logout()
    }
}