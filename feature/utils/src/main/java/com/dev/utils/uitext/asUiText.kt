package com.dev.utils.uitext

import com.dev.utils.uitext.UiText.StringResource
import com.example.designsystem.R
import com.example.domain.utils.DataError
import com.example.domain.utils.Result

fun DataError.asUiText(): UiText {
    return when (this) {
        DataError.UnknownError -> StringResource(R.string.error_unknown)
        is DataError.Logical -> this.message?.let { UiText.DynamicString(it) } ?: StringResource(R.string.error_unknown)

        DataError.Authentication.UserCancelled -> StringResource(R.string.error_user_cancelled)
        DataError.Authentication.UnauthorizedAccess -> StringResource(R.string.error_invalid_credentials)
        DataError.Authentication.UserNotFound -> StringResource(R.string.error_user_not_found)
        DataError.Authentication.UserAlreadyExists -> StringResource(R.string.error_user_already_exists)
        DataError.Authentication.RegistrationFailed -> StringResource(R.string.error_registration_failed)
        DataError.Authentication.SignInFailed -> StringResource(R.string.error_sign_in_failed)
        DataError.Authentication.UsernameAlreadyExists -> StringResource(R.string.error_username_already_exists)

        DataError.Authorization.AccessDenied -> StringResource(R.string.error_access_denied)
        DataError.Authorization.AccountDisabled -> StringResource(R.string.error_account_disabled)
        DataError.Authorization.AccountLocked -> StringResource(R.string.error_account_locked)

        DataError.Data.InvalidData -> StringResource(R.string.error_data_error)
        DataError.Data.NotFound -> StringResource(R.string.error_data_not_found)
        DataError.Data.ParsingError -> StringResource(R.string.error_parsing_error)

        DataError.Local.DiskFull -> StringResource(R.string.error_disk_full)
        DataError.Local.InvalidInput -> StringResource(R.string.error_invalid_input)
        DataError.Local.ConstraintViolation -> StringResource(R.string.error_constraint_violation)
        DataError.Local.RecordNotFound -> StringResource(R.string.error_record_not_found)
        DataError.Local.DatabaseError -> StringResource(R.string.error_database)
        DataError.Local.UnknownError -> StringResource(R.string.error_local_unknown)

        DataError.Network.BadRequest -> StringResource(R.string.error_invalid_request)
        DataError.Network.NoInternetConnection -> StringResource(R.string.error_no_internet)
        DataError.Network.Timeout -> StringResource(R.string.error_timeout)
        DataError.Network.ServerError -> StringResource(R.string.error_server)
        DataError.Network.UnexpectedResponse -> StringResource(R.string.error_unknown)
        DataError.Network.TooManyRequests -> StringResource(R.string.error_too_many_requests)

        DataError.TokenError.TokenNotFound -> StringResource(R.string.error_token_not_found)
        DataError.TokenError.InvalidToken -> StringResource(R.string.error_invalid_token)
        DataError.TokenError.ExpiredToken -> StringResource(R.string.error_session_expired)
        DataError.TokenError.CouldNotGetClaims -> StringResource(R.string.error_could_not_get_claims)
        DataError.TokenError.DecodingFailed -> StringResource(R.string.error_decoding_failed)

        DataError.Validation.InvalidInputs -> StringResource(R.string.error_invalid_inputs)
        DataError.Validation.MissingFields -> StringResource(R.string.error_missing_fields)
        DataError.Validation.InvalidEmailFormat -> StringResource(R.string.error_invalid_email)
        DataError.Validation.WeakPassword -> StringResource(R.string.error_weak_password)
        DataError.Validation.PasswordMismatch -> StringResource(R.string.error_password_mismatch)
        DataError.Validation.ShortFirstName -> StringResource(R.string.error_short_first_name)
        DataError.Validation.ShortLastName -> StringResource(R.string.error_short_last_name)
        DataError.Validation.ShortUsername -> StringResource(R.string.error_short_username)
        DataError.Validation.EmptyFirstName -> StringResource(R.string.error_empty_first_name)
        DataError.Validation.EmptyLastName -> StringResource(R.string.error_empty_last_name)
        DataError.Validation.MustHaveAtLeastOneFieldToUpdate -> StringResource(R.string.error_must_have_at_least_one_field_to_update)
        DataError.Validation.InvalidUri -> StringResource(R.string.error_invalid_uri)
        DataError.Validation.ShortName -> StringResource(R.string.error_short_name)
        DataError.Validation.InvalidOTPFormat -> StringResource(R.string.error_invalid_otp)

        DataError.Verification.InvalidCode -> StringResource(R.string.error_invalid_code)
        DataError.Verification.CodeExpired -> StringResource(R.string.error_code_expired)
        DataError.Verification.TooManyAttempts -> StringResource(R.string.error_too_many_attempts)
        DataError.Verification.VerificationFailed -> StringResource(R.string.error_verification_failed)

        DataError.Location.CouldNotGetTheLocation -> StringResource(R.string.error_could_not_get_location)
        DataError.Location.PermissionDenied -> StringResource(R.string.error_location_permission_denied)
        DataError.Location.LocationDisabled -> StringResource(R.string.error_location_disabled)
        DataError.Location.Timeout -> StringResource(R.string.error_location_timeout)

        DataError.AiService.ConnectionRefused -> StringResource(R.string.error_server)
        DataError.AiService.Timeout -> StringResource(R.string.error_timeout)
        DataError.AiService.Unavailable -> StringResource(R.string.error_server)
        DataError.AiService.ProcessingFailed -> StringResource(R.string.error_server)
    }
}

fun Result.Error<*, DataError>.asErrorUiText(): UiText {
    return error.asUiText()
}
