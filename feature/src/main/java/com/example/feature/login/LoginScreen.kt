package com.example.feature.login

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.util.Log
import android.util.Log.e
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.designsystem.R
import com.example.designsystem.components.AppButton
import com.example.designsystem.components.AppTextField
import com.example.designsystem.components.SigninOptionsButton
import com.example.designsystem.components.TextFieldType
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.spacing
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.log

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
    onCloseClicked: () -> Unit,
    navigateToSignUp: () -> Unit,
    navigateToForgetPassword: () -> Unit
) {

    val uiState = viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val webClientId = stringResource(id = R.string.web_server_id)
    val callbackManager = CallbackManager.Factory.create()
    fun handleGoogleSignIn() {
        scope.launch {
            try {
                Log.d("GoogleSignIn", "Package Name: ${context.packageName}")
                val credentialManager = CredentialManager.create(context)

                val googleIdOption =
                    GetGoogleIdOption.Builder().setFilterByAuthorizedAccounts(false)
                        .setAutoSelectEnabled(false).setServerClientId(webClientId).build()

                val request =
                    GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()

                val result = credentialManager.getCredential(
                    request = request, context = context
                )
                // Extract the token
                when (val credential = result.credential) {
                    is CustomCredential -> {
                        if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                            val googleIdTokenCredential =
                                GoogleIdTokenCredential.createFrom(credential.data)

                            Log.d("GoogleSignIn", "✓ Successfully parsed credential")
                            Log.d("GoogleSignIn", "User: ${googleIdTokenCredential.displayName}")
                            Log.d("GoogleSignIn", "Email: ${googleIdTokenCredential.id}")
                            Log.d("GoogleSignIn", "Token : ${googleIdTokenCredential.idToken}")

                            viewModel.onGoogleSignIn(googleIdTokenCredential.idToken)
                        } else {
                            e("GoogleSignIn", "✗ Invalid credential type")
                            Toast.makeText(
                                context,
                                "Sign in failed: Invalid credential type",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                    }

                    else -> {
                        e(
                            "GoogleSignIn",
                            "✗ Unknown credential class: ${credential::class.simpleName}"
                        )
                    }
                }
            } catch (e: Exception) {
                e("GoogleSignIn", "✗ Failed to parse credential", e)
                Toast.makeText(
                    context, "Sign in failed: ${e.message}", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    val performLogin = rememberFacebookLogin(
        callbackManager = callbackManager,
        onSuccess = { token ->
            Log.d("FacebookSignIn", "Token: $token")
        },
        onError = { error ->

        }
    )

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                LoginEvent.NavigateToForgotPassword -> {
                    navigateToForgetPassword()
                }

                LoginEvent.NavigateToHome -> TODO()
                LoginEvent.NavigateToSignup -> {
                    navigateToSignUp()
                }

                is LoginEvent.ShowAuthError -> TODO()
                LoginEvent.ContinueWithFacebook -> {
                    performLogin()
                }

                LoginEvent.ContinueWithGoogle -> {
                    handleGoogleSignIn()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.log_in),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }, navigationIcon = {
                    Box(
                        modifier = Modifier
                            .padding(start = MaterialTheme.spacing.md)
                            .size(MaterialTheme.spacing.xxl)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                shape = CircleShape
                            )
                            .clickable { onCloseClicked() }, contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(id = R.string.close),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }, actions = {
                    Spacer(modifier = Modifier.size(56.dp))
                }, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))

            Text(
                text = stringResource(id = R.string.continue_using_your),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(20.dp))

            AppTextField(
                value = uiState.value.email,
                onValueChange = { viewModel.onEmailChange(it) },
                placeholder = stringResource(id = R.string.email),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))

            AppTextField(
                value = uiState.value.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                placeholder = stringResource(id = R.string.password),
                fieldType = TextFieldType.PASSWORD,
                isPasswordVisible = uiState.value.isPasswordVisible,
                onPasswordVisibilityChecked = { viewModel.onPasswordVisibilityCheck() },
                modifier = Modifier.fillMaxWidth()
            )

            RememberMeAndForgetPasswordSection(
                checked = uiState.value.isRememberMeChecked,
                onRememberMeCheckedChange = { viewModel.onRememberMeChecked() },
                onForgetPasswordClicked = { viewModel.onForgotPasswordClicked() })

            AppButton(
                onClick = {
                    viewModel.onLoginClicked(
                        uiState.value.email, uiState.value.password
                    )
                }, text = stringResource(id = R.string.log_in), modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))

            OrSignInWithText()

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))

            SigninOptionsButton(
                onClick = { viewModel.onGoogleSignInClicked() },
                text = stringResource(id = R.string.continue_with_google),
                icon = R.drawable.google_icon,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.sm))

            SigninOptionsButton(
                onClick = { viewModel.onFacebookSigninClicked() },
                text = stringResource(id = R.string.continue_with_facebook),
                icon = R.drawable.facebook_icon,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.xl))

            ByLoggingSection()
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))

            Row(
                horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.dont_have_an_account_yet) + " ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(id = R.string.create_an_account),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { viewModel.onCreateAccountClicked() })
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))
        }
    }
}

@Composable
fun rememberFacebookLogin(
    callbackManager: CallbackManager,
    onSuccess: (String) -> Unit,
    onError: (String) -> Unit = {},
    onCancel: () -> Unit = {}
): () -> Unit {
    val context = LocalContext.current
    val loginManager = LoginManager.getInstance()

    DisposableEffect(Unit) {
        loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onCancel() {
                Log.d("FacebookSignIn", "Entered Cancel")

                onCancel()
                Toast.makeText(context, "Login Cancelled", Toast.LENGTH_SHORT).show()
            }

            override fun onError(error: FacebookException) {
                Log.d("FacebookSignIn", "Entered Error")

                val msg = error.message ?: "Unknown Error"
                onError(msg)
                Toast.makeText(context, "Error: $msg", Toast.LENGTH_LONG).show()
            }

            override fun onSuccess(result: LoginResult) {
                Log.d("FacebookSignIn", "Entered Success")

                onSuccess(result.accessToken.token)
            }
        })
        onDispose {
            loginManager.unregisterCallback(callbackManager)
        }
    }

    return {
        val activity = context.findActivity()
        if (activity != null) {
            // Log to verify the button actually works
            Log.d("FacebookSignIn", "Activity found! Starting Login...")

            loginManager.logInWithReadPermissions(
                activity,
                listOf("public_profile", "email")
            )
        } else {
            Log.d("FacebookSignIn", "Context is not an Activity! Login failed.")
        }
    }
}

@Composable
fun FacebookSignInHandler(
    context: Context, enabled: Boolean = true, onTokenReceived: (String) -> Unit
) {
    val callbackManager = remember { CallbackManager.Factory.create() }

    val loginManager = LoginManager.getInstance()

    DisposableEffect(Unit) {
        loginManager.registerCallback(
            callbackManager, object : FacebookCallback<LoginResult> {
                override fun onCancel() {
                    Toast.makeText(context, "Facebook login cancelled", Toast.LENGTH_SHORT).show()
                }

                override fun onError(error: FacebookException) {
                    Toast.makeText(
                        context, "Facebook login failed: ${error.message}", Toast.LENGTH_LONG
                    ).show()
                }

                override fun onSuccess(result: LoginResult) {
                    val accessToken = result.accessToken.token
                    onTokenReceived(accessToken)
                }

            })
        onDispose { loginManager.unregisterCallback(callbackManager) }
    }

    rememberLauncherForActivityResult(
        contract = loginManager.createLogInActivityResultContract(callbackManager)
    ) {
        // Result is handled by the callback above
    }
}


@Composable
fun ByLoggingSection(modifier: Modifier = Modifier) {
    val annotatedString = buildAnnotatedString {
        val regularStyle = SpanStyle(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = MaterialTheme.typography.bodySmall.fontSize
        )
        val highlightedStyle = SpanStyle(
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium,
            fontSize = MaterialTheme.typography.bodySmall.fontSize
        )

        withStyle(regularStyle) {
            append(stringResource(id = R.string.by_logging_you_agree_to_our) + " ")
        }
        withStyle(highlightedStyle) {
            append(stringResource(id = R.string.terms_and_conditions))
        }
        withStyle(regularStyle) {
            append(" " + stringResource(id = R.string.and) + " ")
        }
        withStyle(highlightedStyle) {
            append(stringResource(id = R.string.privacy_policy))
        }
    }

    Text(
        text = annotatedString,
        style = MaterialTheme.typography.bodySmall,
        textAlign = TextAlign.Center,
        modifier = modifier.padding(horizontal = MaterialTheme.spacing.xs)
    )
}


@Composable
fun OrSignInWithText(modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
        Text(
            text = stringResource(id = R.string.or_sign_in_with),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    }
}

@Composable
fun RememberMeAndForgetPasswordSection(
    checked: Boolean,
    onRememberMeCheckedChange: (Boolean) -> Unit,
    onForgetPasswordClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = onRememberMeCheckedChange,
            )
            Text(
                text = stringResource(R.string.remember_me),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Text(
            text = stringResource(R.string.forgot_password),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.clickable { onForgetPasswordClicked() })
    }
}

@Preview(
    name = "Light Mode", group = "Login Screen", device = "id:pixel_9", showSystemUi = true
)
@Preview(
    name = "Dark Mode",
    group = "Login Screen",
    device = "id:pixel_9",
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun LoginScreenPreview() {
    TravioTheme {
        LoginScreen(
            onCloseClicked = {},
            navigateToSignUp = { }
        ) {}
    }
}

@Preview(
    name = "Arabic Light Mode",
    group = "Login Screen",
    device = "id:pixel_9",
    showSystemUi = true,
    locale = "ar-rEG"
)
@Preview(
    name = "Arabic Dark Mode",
    group = "Login Screen",
    device = "id:pixel_9",
    showSystemUi = true,
    locale = "ar-rEG",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun LoginScreenPreviewArabic() {
    TravioTheme {
        LoginScreen(
            onCloseClicked = {},
            navigateToSignUp = { }
        ) {}
    }
}
