package com.example.network.api

import com.example.network.dto.user_managment.GetUserResponse
import com.example.network.dto.user_managment.UpdateProfileRequest
import com.example.network.dto.user_managment.UpdateProfileResponse
import com.example.network.dto.user_managment.UploadImageResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part

interface UserManagementApi {
    @PUT("Profile/update-profile")
    suspend fun updateProfileData(@Body request: UpdateProfileRequest): UpdateProfileResponse

    @Multipart
    @POST("Profile/upload-image")
    suspend fun updateProfilePic(@Part profilePic: MultipartBody.Part): UploadImageResponse

    @GET("Profile")
    suspend fun getCurrentUser(): GetUserResponse
}