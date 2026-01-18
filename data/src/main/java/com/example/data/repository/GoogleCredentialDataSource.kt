package com.example.data.repository

import android.content.Context
import com.example.domain.utils.Result
import com.example.domain.utils.DataError

interface GoogleCredentialDataSource {
    suspend fun getGoogleIdToken(context: Context, webClientId: String): Result<String, DataError>
}