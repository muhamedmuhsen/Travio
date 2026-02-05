package com.example.data.repository

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import com.example.domain.utils.Result
import com.example.domain.utils.DataError
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import javax.inject.Inject

class GoogleCredentialDataSourceImpl @Inject constructor(
) {

    suspend fun getGoogleIdToken(
        context: Context,
        webClientId: String
    ): Result<String, DataError> {
        try {


            val credentialManager = CredentialManager.create(context)

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setAutoSelectEnabled(false)
                .setServerClientId(webClientId)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = context
            )
            return parseCredentialResult(result)

        } catch (_: GetCredentialCancellationException) {
            return Result.Error(DataError.Authentication.UserCancelled)
        } catch (_: NoCredentialException) {
            return Result.Error(DataError.Authentication.SignInFailed)
        } catch (e: Exception) {
            return Result.Error(DataError.Data.UnknownError)
        }
    }

    private fun parseCredentialResult(
        credentialResult: GetCredentialResponse
    ): Result<String, DataError> {
        return when (val credential = credentialResult.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)
                        Result.Success(googleIdTokenCredential.idToken)
                    } catch (e: GoogleIdTokenParsingException) {
                        Result.Error(DataError.Authentication.SignInFailed)
                    }
                } else {
                    Result.Error(DataError.Authentication.SignInFailed)
                }
            }

            else -> {
                Result.Error(DataError.Authentication.SignInFailed)
            }
        }
    }

}