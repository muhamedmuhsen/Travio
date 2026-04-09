package com.dev.utils.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException

// Credential Manager requires an Activity context to display the sign-in bottom sheet,
// so this helper lives in the UI layer rather than in a ViewModel or data-source.
object GoogleCredentialHelper {

    suspend fun getGoogleIdToken(
        context: Context,
        webClientId: String
    ): Result<String, DataError> {
        return try {
            val credentialManager = CredentialManager.create(context)

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setAutoSelectEnabled(false)
                .setServerClientId(webClientId)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val response = credentialManager.getCredential(request = request, context = context)
            parseCredentialResponse(response)
        } catch (_: GetCredentialCancellationException) {
            Result.Error(DataError.Authentication.UserCancelled)
        } catch (_: NoCredentialException) {
            Result.Error(DataError.Authentication.SignInFailed)
        } catch (_: Exception) {
            Result.Error(DataError.UnknownError)
        }
    }

    private fun parseCredentialResponse(response: GetCredentialResponse): Result<String, DataError> {
        val credential = response.credential
        if (credential !is CustomCredential) return Result.Error(DataError.Authentication.SignInFailed)
        if (credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            return Result.Error(DataError.Authentication.SignInFailed)
        }
        return try {
            val token = GoogleIdTokenCredential.createFrom(credential.data)
            Result.Success(token.idToken)
        } catch (_: GoogleIdTokenParsingException) {
            Result.Error(DataError.Authentication.SignInFailed)
        }
    }
}
