package com.example.network.api

import com.example.network.dto.user_managment.UpdateProfileRequest
import com.example.network.dto.user_managment.UpdateProfileResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface UserManagementApi {
    @POST("user/update-profile")
    suspend fun updateUserProfile(@Body request: UpdateProfileRequest): UpdateProfileResponse
}