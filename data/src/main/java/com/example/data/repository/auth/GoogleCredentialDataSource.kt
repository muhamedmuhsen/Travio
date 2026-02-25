package com.example.data.repository.auth

import android.content.Context
import com.example.domain.utils.DataError
import com.example.domain.utils.Result

interface GoogleCredentialDataSource {
    suspend fun getGoogleIdToken(
        context: Context,
        webClientId: String
    ): Result<String, DataError>
}
