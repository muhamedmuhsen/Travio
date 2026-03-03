package com.example.domain.usecase.usermanagement

import com.example.domain.model.auth.User
import com.example.domain.repository.usermanagement.UserManagementRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import javax.inject.Inject

class GetUserUseCase @Inject constructor(private val repository: UserManagementRepository) {
    suspend operator fun invoke(): Result<User, DataError> = repository.getUser()
}
