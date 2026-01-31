package com.example.data.repository

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import com.example.domain.utils.Result
import com.example.domain.utils.DataError
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import javax.inject.Inject

class GoogleCredentialDataSourceImpl @Inject constructor() : GoogleCredentialDataSource {

    override suspend fun getGoogleIdToken(
        context: Context, webClientId: String
    ): Result<String, DataError> {
        val credentialManager = CredentialManager.create(context)
        try {
            val googleIdOption = GetGoogleIdOption.Builder().setFilterByAuthorizedAccounts(false)
                .setAutoSelectEnabled(false).setServerClientId(webClientId).build()

            val request = GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()
            val result = credentialManager.getCredential(request = request, context = context)
            when (val credential = result.credential) {
                is CustomCredential -> {
                    if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)
                        return Result.Success(googleIdTokenCredential.idToken)
                    } else {
                        return Result.Error(DataError.Authentication.SignInFailed)
                    }
                }

                else -> {
                    return Result.Error(DataError.Authentication.SignInFailed)
                }
            }
        } catch (_: GetCredentialCancellationException) {
            return Result.Error(DataError.Authentication.UserCancelled)
        } catch (_: NoCredentialException) {
            return Result.Error(DataError.Authentication.SignInFailed)
        } catch (e: Exception) {
            return Result.Error(DataError.Data.UnknownError)
        }
    }
}