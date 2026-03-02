package com.example.feature.login

import android.content.res.Configuration
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.designsystem.components.AppButton
import com.example.designsystem.components.AppTextField
import com.example.designsystem.components.ErrorSnackBar
import com.example.designsystem.components.SigninOptionsButton
import com.example.designsystem.components.TextFieldType
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.spacing
import com.example.feature.auth.BuildConfig
import com.example.feature.auth.R
import com.example.feature.login.components.ByLoggingSection
import com.example.feature.login.components.OrSignInWithText
import com.example.feature.login.components.RememberMeAndForgetPasswordSection
import timber.log.Timber
import com.example.designsystem.R as DesignSystemR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
    onCloseClicked: () -> Unit,
    navigateToSignUp: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToForgetPassword: () -> Unit
) {
    val uiState = viewModel.state.collectAsStateWithLifecycle()
    val webClientId = BuildConfig.GOOGLE_WEB_CLIENT_ID
    val context = LocalContext.current
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            kotlinx.coroutines.delay(3000)
            errorMessage = null
        }
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                LoginEvent.NavigateToForgotPassword -> navigateToForgetPassword()
                LoginEvent.NavigateToHome -> navigateToHome()
                LoginEvent.NavigateToSignup -> navigateToSignUp()
                is LoginEvent.ShowAuthError -> {
                    errorMessage = event.message.asString(context)
                }

                LoginEvent.ContinueWithFacebook -> {
                    // performLogin()
                }

                LoginEvent.ContinueWithGoogle -> {
                    viewModel.onGoogleSignInClicked(context, webClientId)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = {
            errorMessage?.let { message ->
                ErrorSnackBar(text = message)
            }
        },
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
                },
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .padding(start = MaterialTheme.spacing.md)
                            .size(MaterialTheme.spacing.xxl)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                shape = CircleShape
                            )
                            .clickable { onCloseClicked() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(id = R.string.close),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                actions = {
                    Spacer(modifier = Modifier.size(56.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
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
                isError = uiState.value.isEmailError,
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
                isError = uiState.value.isPasswordError,
                onPasswordVisibilityChecked = { viewModel.onPasswordVisibilityCheck() },
                modifier = Modifier.fillMaxWidth()
            )

            RememberMeAndForgetPasswordSection(
                checked = uiState.value.isRememberMeChecked,
                onRememberMeCheckedChange = { viewModel.onRememberMeChecked() },
                onForgetPasswordClicked = { viewModel.onForgotPasswordClicked() }
            )

            AppButton(
                onClick = {
                    viewModel.onLoginClicked()
                },
                text = stringResource(id = R.string.log_in),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))

            OrSignInWithText()

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))

            SigninOptionsButton(
                onClick = {
                    Timber.d("Google sign-in button clicked")
                    viewModel.onGoogleSignInClicked(context, webClientId)
                },
                text = stringResource(id = R.string.continue_with_google),
                icon = DesignSystemR.drawable.google_icon,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.sm))

            SigninOptionsButton(
                onClick = { /*viewModel.onFacebookSigninClicked() */ },
                text = stringResource(id = R.string.continue_with_facebook),
                icon = DesignSystemR.drawable.facebook_icon,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.xl))
            ByLoggingSection()
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
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
                    modifier = Modifier.clickable { viewModel.onCreateAccountClicked() }
                )
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))
        }
    }
}

@Preview(
    name = "Light Mode",
    group = "Login Screen",
    device = "id:pixel_9",
    showSystemUi = true
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
            navigateToSignUp = { },
            navigateToHome = {}
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
            navigateToSignUp = { },
            navigateToHome = {}
        ) {}
    }
}
