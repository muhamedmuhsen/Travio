package com.example.data.repository.user_management

import com.example.data.local.datastore.PreferencesManager
import com.example.data.local.datastore.SecureTokenStorage
import com.example.domain.model.User
import com.example.domain.repository.user_management.UserManagementRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import com.example.network.api.UserManagementApi
import com.example.network.dto.user_managment.UpdateProfileRequest
import com.example.network.dto.user_managment.UpdateProfileResponse
import javax.inject.Inject

class UserManagementRepositoryImpl @Inject constructor(
    private val api: UserManagementApi,
    private val secureTokenStorage: SecureTokenStorage,
    private val preferencesManager: PreferencesManager
) : UserManagementRepository {
    override suspend fun getUser(): Result<User, DataError> {
        return Result.Success(
            User(
                firstName = "Osama",
                lastName = "Mahmoud",
                username = "osama_mahmoud",
                email = "Osama.mahmoud0@gmail.com",
                profilePictureUrl = null
            )
        )
    }


    override suspend fun updateProfile(
        firstName: String?,
        lastName: String?,
        email: String?,
        profilePictureUrl: String?
    ): Result<Unit, DataError> {
        try {
            if (hasAtLeastOneField(firstName, lastName, email, profilePictureUrl))
                return Result.Error(DataError.Validation.MustHaveAtLeastOneFieldToUpdate)

            val request = UpdateProfileRequest(
                firstName = firstName,
                lastName = lastName,
                email = email,
                profilePictureUrl = profilePictureUrl
            )

            val response = api.updateUserProfile(request)

            handlePostUpdateProfilePersistence(response)

            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Error(DataError.Data.UnknownError)
        }
    }

    private fun hasAtLeastOneField(
        firstName: String?,
        lastName: String?,
        email: String?,
        profilePictureUrl: String?
    ): Boolean {
        return !listOf(
            firstName,
            lastName,
            email,
            profilePictureUrl
        ).all { it == null }
    }

    private suspend fun handlePostUpdateProfilePersistence(response: UpdateProfileResponse) {
        secureTokenStorage.saveTokens(response.accessToken, response.refreshToken)
        preferencesManager.setLoggedIn(true)
    }
}