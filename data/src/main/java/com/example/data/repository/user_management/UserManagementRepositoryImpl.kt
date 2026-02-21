package com.example.data.repository.user_management

import android.content.Context
import android.net.Uri
import com.example.data.local.security.SecureTokenStorage
import com.example.domain.model.User
import com.example.domain.repository.prefernces.PreferencesManager
import com.example.domain.repository.user_management.UserManagementRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import com.example.network.api.UserManagementApi
import com.example.network.dto.user_managment.UpdateProfileRequest
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import com.example.data.mapper.toDomain
import com.example.data.BuildConfig
import com.example.data.utils.toMultipartBodyPart
import dagger.hilt.android.qualifiers.ApplicationContext
import androidx.core.net.toUri

class UserManagementRepositoryImpl @Inject constructor(
    private val api: UserManagementApi,
    @ApplicationContext private val context: Context
) : UserManagementRepository {

    override suspend fun getUser(): Result<User, DataError> {
        return try {
            val result = api.getCurrentUser()
            val user = result.toDomain()
            Result.Success(user)
        } catch (e: HttpException) {
            when (e.code()) {
                401 -> Result.Error(DataError.Authentication.UnauthorizedAccess)
                404 -> Result.Error(DataError.Authentication.UserNotFound)
                429 -> Result.Error(DataError.Network.TooManyRequests)
                in 500..599 -> Result.Error(DataError.Network.ServerError)
                else -> Result.Error(DataError.Network.UnexpectedResponse)
            }
        } catch (e: IOException) {
            Result.Error(DataError.Network.NoInternetConnection)
        } catch (e: Exception) {
            Result.Error(DataError.Data.UnknownError)
        }
    }


    override suspend fun updateProfile(
        firstName: String,
        lastName: String,
        username: String
    ): Result<User, DataError> {
        try {
            val request = UpdateProfileRequest(
                firstName = firstName, lastName = lastName,
                username = username
            )
            val response = api.updateProfileData(request)

            return Result.Success(response.data.toDomain())
        } catch (e: Exception) {
            return Result.Error(DataError.Data.UnknownError)
        }
    }

    override suspend fun updateProfilePic(imageUri: String): Result<String, DataError> {
        try {
            val uri = imageUri.toUri()
            val multipartBody =
                uri.toMultipartBodyPart(context) ?: return Result.Error(DataError.Data.UnknownError)
            val response = api.updateProfilePic(multipartBody)
            val absoluteUrl = "${BuildConfig.IMAGE_BASE_URL}${response.data}"
            return Result.Success(absoluteUrl)
        } catch (e: Exception) {
            return Result.Error(DataError.Network.UnexpectedResponse)
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
}