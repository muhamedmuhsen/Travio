package com.example.data.repository

import android.content.Context
import com.example.common.errorhandler.AppError
import com.example.common.errorhandler.Result

interface GoogleCredentialDataSource {
    suspend fun getGoogleIdToken(context: Context, webClientId: String): Result<String, AppError>
}