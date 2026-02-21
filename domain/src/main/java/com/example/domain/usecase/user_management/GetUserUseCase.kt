package com.example.domain.usecase.user_management

import com.example.domain.model.User
import com.example.domain.repository.user_management.UserManagementRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import javax.inject.Inject

class GetUserUseCase @Inject constructor(private val repository: UserManagementRepository) {
    suspend operator fun invoke(): Result<User, DataError> = repository.getUser()
}


