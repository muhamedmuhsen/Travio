package com.example.network.api

import com.example.domain.utils.DataError
import com.example.network.dto.user_managment.GetUserResponse
import com.example.network.dto.user_managment.UpdateProfileRequest
import com.example.network.dto.user_managment.UpdateProfileResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface UserManagementApi {
    @PUT("Profile/update-profile")
    suspend fun updateProfileData(@Body request: UpdateProfileRequest): UpdateProfileResponse

    @POST("Profile/upload-image")
    suspend fun updateProfilePic()
    @GET("Profile")
    suspend fun getCurrentUser(): GetUserResponse
}