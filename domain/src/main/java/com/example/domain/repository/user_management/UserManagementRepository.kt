package com.example.domain.repository.user_management

import com.example.domain.model.auth.User
import com.example.domain.utils.DataError
import com.example.domain.utils.Result

interface UserManagementRepository {

    suspend fun getUser(): Result<User, DataError>

    suspend fun updateProfile(
        firstName: String,
        lastName: String,
        username: String
    ): Result<User, DataError>

    suspend fun updateProfilePic(imageUri: String): Result<String, DataError>
}