package com.example.domain.repository.prefernces

import com.example.domain.model.auth.UserCredentials

interface CredentialsManager {
    suspend fun saveCredentials(
        email: String,
        password: String,
        rememberMe: Boolean
    )
    suspend fun getCredentials(): UserCredentials?
    suspend fun getSavedEmail(): String?
    suspend fun getSavedPassword(): String?
    suspend fun isRememberMeEnabled(): Boolean
    suspend fun setRememberMe(isChecked: Boolean)
    suspend fun clearCredentials()
}
