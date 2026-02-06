package ui.text

import com.example.designsystem.R
import com.example.domain.utils.DataError
import com.example.domain.utils.Result

fun DataError.asUiText(): UiText {
    return when (this) {
        DataError.Authentication.UserCancelled -> UiText.StringResource(R.string.error_user_cancelled)
        DataError.Authentication.UnauthorizedAccess -> UiText.StringResource(R.string.error_invalid_credentials)
        DataError.Authentication.UserNotFound -> UiText.StringResource(R.string.error_user_not_found)
        DataError.Authentication.UserAlreadyExists -> UiText.StringResource(R.string.error_user_already_exists)
        DataError.Authentication.RegistrationFailed -> UiText.StringResource(R.string.error_registration_failed)
        DataError.Authentication.SignInFailed -> UiText.StringResource(R.string.error_sign_in_failed)

        DataError.Authorization.AccessDenied -> UiText.StringResource(R.string.error_access_denied)
        DataError.Authorization.AccountDisabled -> UiText.StringResource(R.string.error_account_disabled)
        DataError.Authorization.AccountLocked -> UiText.StringResource(R.string.error_account_locked)

        DataError.Data.InvalidData -> UiText.StringResource(R.string.error_data_error)
        DataError.Data.NotFound -> UiText.StringResource(R.string.error_data_not_found)
        DataError.Data.ParsingError -> UiText.StringResource(R.string.error_parsing_error)
        DataError.Data.UnknownError -> UiText.StringResource(R.string.error_unknown)

        DataError.Local.DiskFull -> UiText.StringResource(R.string.error_disk_full)

        DataError.Network.BadRequest -> UiText.StringResource(R.string.error_invalid_request)
        DataError.Network.NoInternetConnection -> UiText.StringResource(R.string.error_no_internet)
        DataError.Network.Timeout -> UiText.StringResource(R.string.error_timeout)
        DataError.Network.ServerError -> UiText.StringResource(R.string.error_server)
        DataError.Network.UnexpectedResponse -> UiText.StringResource(R.string.error_unknown)
        DataError.Network.TooManyRequests -> UiText.StringResource(R.string.error_too_many_requests)

        DataError.TokenError.TokenNotFound -> UiText.StringResource(R.string.error_token_not_found)
        DataError.TokenError.InvalidToken -> UiText.StringResource(R.string.error_invalid_token)
        DataError.TokenError.ExpiredToken -> UiText.StringResource(R.string.error_session_expired)
        DataError.TokenError.CouldNotGetClaims -> UiText.StringResource(R.string.error_could_not_get_claims)
        DataError.TokenError.DecodingFailed -> UiText.StringResource(R.string.error_decoding_failed)

        DataError.Validation.InvalidInputs -> UiText.StringResource(R.string.error_invalid_inputs)
        DataError.Validation.MissingFields -> UiText.StringResource(R.string.error_missing_fields)
        DataError.Validation.InvalidEmailFormat -> UiText.StringResource(R.string.error_invalid_email)
        DataError.Validation.WeakPassword -> UiText.StringResource(R.string.error_weak_password)
        DataError.Validation.PasswordMismatch -> UiText.StringResource(R.string.error_password_mismatch)
        DataError.Validation.ShortName -> UiText.StringResource(R.string.error_short_name)
        DataError.Validation.InvalidOTPFormat -> UiText.StringResource(R.string.error_invalid_otp)

        DataError.Verification.InvalidCode -> UiText.StringResource(R.string.error_invalid_code)
        DataError.Verification.CodeExpired -> UiText.StringResource(R.string.error_code_expired)
        DataError.Verification.TooManyAttempts -> UiText.StringResource(R.string.error_too_many_attempts)
        DataError.Verification.VerificationFailed -> UiText.StringResource(R.string.error_verification_failed)
    }
}

fun Result.Error<*, DataError>.asErrorUiText(): UiText {
    return error.asUiText()
}