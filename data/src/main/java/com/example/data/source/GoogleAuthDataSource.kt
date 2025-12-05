package com.example.data.source

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.example.data.di.WebClientId
import com.example.network.dto.auth.social.GoogleUserDto
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import javax.inject.Inject

interface GoogleAuthDataSource {
    suspend fun signIn(activity: ComponentActivity): GoogleUserDto
}

class GoogleAuthDataSourceImpl @Inject constructor(
    @WebClientId private val webClientId: String
) : GoogleAuthDataSource {
    private fun handleSignInResult(result: GetCredentialResponse): GoogleUserDto {
        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)
                        Log.d("TAG", "handleSignInResult: ${googleIdTokenCredential.id}")
                        return GoogleUserDto(
                            idToken = googleIdTokenCredential.idToken,
                            displayName = googleIdTokenCredential.displayName,
                            email = googleIdTokenCredential.id,
                            profilePicUrl = googleIdTokenCredential.profilePictureUri?.toString()
                        )

                    } catch (e: Exception) {
                        throw Exception("Failed to parse Google credential: ${e.message}")

                    }
                } else {
                    throw Exception("Unexpected credential type: ${credential.type}")

                }
            }

            else -> {
                throw Exception("Unexpected credential class: ${credential::class.java.name}")
            }
        }

    }

    override suspend fun signIn(activity: ComponentActivity): GoogleUserDto {
        val credentialManager = CredentialManager.create(activity)

        try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId).build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                request = request, context = activity
            )
            return handleSignInResult(result)
        } catch (e: Exception) {
            throw e
        }
    }

}