package com.example.data.repository.user_management

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import com.example.data.local.datastore.SecureTokenStorage
import com.example.domain.model.User
import com.example.domain.repository.prefernces.PreferencesManager
import com.example.domain.repository.user_management.UserManagementRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import com.example.network.api.UserManagementApi
import com.example.network.dto.user_managment.UpdateProfileRequest
import com.example.network.dto.user_managment.UpdateProfileResponse
import javax.inject.Inject
import androidx.core.net.toUri
import com.example.data.mapper.toDomain
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.http.Multipart
import java.io.File

class UserManagementRepositoryImpl @Inject constructor(
    private val api: UserManagementApi,
    private val secureTokenStorage: SecureTokenStorage,
    private val preferencesManager: PreferencesManager,
) : UserManagementRepository {

    override suspend fun getUser(): Result<User, DataError> {
        val result = api.getCurrentUser()
        val user = result.toDomain()
        // save user data to local store
        return Result.Success(user)
    }


    override suspend fun updateProfile(
        firstName: String?,
        lastName: String?,
        email: String?,
        profilePictureUrl: String?
    ): Result<Unit, DataError> {
        try {

//            val uri = profilePictureUrl?.toUri()
//            val profileImagePart = toMultipartBodyPart(uri)

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

//    private fun toMultipartBodyPart(uri: Uri): MultipartBody.Part {
//        val contentResolver = context.contentResolver
//        val mimeType = contentResolver.getType(uri) ?: "image/*"
//        val inputStream = contentResolver.openInputStream(uri)
//            ?: throw IllegalStateException("Cannot open input stream")
//        val file = File(context.cacheDir, "profile_${System.currentTimeMillis()}.jpg")
//        file.outputStream().use { outputStream ->
//            inputStream.copyTo(outputStream)
//        }
//        val requestBody = file.asRequestBody(mimeType.toMediaTypeOrNull())
//        return MultipartBody.Part.createFormData("profilePicture", file.name, requestBody)
//    }

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