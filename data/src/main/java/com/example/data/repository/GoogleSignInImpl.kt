package com.example.data.repository

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import com.example.common.errorhandler.AppError
import com.example.common.errorhandler.Result
import com.example.domain.repository.auth.GoogleSignIn
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GoogleSignInImpl @Inject constructor(@ActivityContext private val context: Context) :
    GoogleSignIn {
    private val credentialManager = CredentialManager.create(context)

    override suspend fun signIn(
        webClientId: String
    ): Result<String, AppError> {
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
                        return Result.Error(AppError.Authentication.SignInFailed)
                    }
                }

                else -> {
                    return Result.Error(AppError.Authentication.SignInFailed)
                }
            }
        } catch (_: GetCredentialCancellationException) {
            return Result.Error(AppError.Authentication.UserCancelled)
        } catch (_: NoCredentialException) {
            return Result.Error(AppError.Authentication.SignInFailed)
        } catch (e: Exception) {
            return Result.Error(AppError.Unknown(e.localizedMessage ?: "Unknown Error"))
        }
    }
}