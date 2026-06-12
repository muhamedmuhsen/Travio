package com.example.feature.signup

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dev.utils.auth.GoogleCredentialHelper
import com.example.designsystem.components.AppButton
import com.example.designsystem.components.AppTextField
import com.example.designsystem.components.ErrorSnackBar
import com.example.designsystem.components.SigninOptionsButton
import com.example.designsystem.components.TextFieldType
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.spacing
import com.example.domain.utils.Result
import com.example.feature.auth.BuildConfig
import com.example.feature.auth.R
import com.example.feature.login.components.OrSignInWithText
import kotlinx.coroutines.launch
import com.example.designsystem.R as DesignSystemR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(
    modifier: Modifier = Modifier,
    viewModel: SignupViewModel = hiltViewModel(),
    onCloseClicked: () -> Unit,
    navigateToLogin: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToVerifyEmail: (String) -> Unit
) {
    val uiState = viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val webClientId = BuildConfig.GOOGLE_WEB_CLIENT_ID
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
                SignupEvent.NavigateToLogin -> navigateToLogin()
                SignupEvent.NavigateToHome -> navigateToHome()
                is SignupEvent.ShowAuthError -> errorMessage = event.message.asString(context)
                SignupEvent.NavigateToVerifyEmail -> navigateToVerifyEmail(uiState.value.email)
            }
        }
    }

    SignupScreenContent(
        state = uiState.value,
        errorMessage = errorMessage,
        onCloseClicked = onCloseClicked,
        onFirstNameChange = viewModel::onFirstNameChange,
        onLastNameChange = viewModel::onLastNameChange,
        onUsernameChange = viewModel::onUsernameChange,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onPasswordVisibilityCheck = viewModel::onPasswordVisibilityCheck,
        onSignupClicked = viewModel::onSignupClicked,
        onGoogleSignInClicked = {
            viewModel.onGoogleSignInStarted()
            scope.launch {
                when (val result = GoogleCredentialHelper.getGoogleIdToken(context, webClientId)) {
                    is Result.Success -> viewModel.onGoogleSignInResult(result.data)
                    is Result.Error -> viewModel.onGoogleSignInError(result.error)
                }
            }
        },
        onNavigateToLogin = navigateToLogin,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreenContent(
    state: SignupUiState,
    errorMessage: String?,
    onCloseClicked: () -> Unit,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordVisibilityCheck: () -> Unit,
    onSignupClicked: () -> Unit,
    onGoogleSignInClicked: () -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                        text = stringResource(id = R.string.create_an_account),
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
                            contentDescription = stringResource(id = DesignSystemR.string.close),
                            modifier = Modifier.size(MaterialTheme.spacing.lg)
                        )
                    }
                },
                actions = {
                    Spacer(modifier = Modifier.size(MaterialTheme.spacing.xxxl + MaterialTheme.spacing.xs))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = MaterialTheme.spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))

            AppTextField(
                value = state.firstname,
                onValueChange = onFirstNameChange,
                placeholder = stringResource(id = R.string.first_name),
                isError = state.isFirstNameError,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))

            AppTextField(
                value = state.lastname,
                onValueChange = onLastNameChange,
                placeholder = stringResource(id = R.string.last_name),
                isError = state.isLastNameError,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))

            AppTextField(
                value = state.username,
                onValueChange = onUsernameChange,
                placeholder = stringResource(id = R.string.username),
                isError = state.isUsernameError,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))
            AppTextField(
                value = state.email,
                onValueChange = onEmailChange,
                placeholder = stringResource(id = R.string.email),
                isError = state.isEmailError,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))

            AppTextField(
                value = state.password,
                onValueChange = onPasswordChange,
                placeholder = stringResource(id = R.string.password),
                fieldType = TextFieldType.PASSWORD,
                isPasswordVisible = state.isPasswordVisible,
                onPasswordVisibilityChecked = onPasswordVisibilityCheck,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))

            PasswordRulesText(
                textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                textStyle = MaterialTheme.typography.bodySmall,
                isMinLengthMet = state.isMinLengthMet,
                isLetterAndNumberMet = state.isLetterAndNumberMet,
                isUpperCaseMet = state.isUpperCaseMet,
                isSpecialCharMet = state.isSpecialCharMet
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))

            AppButton(
                onClick = onSignupClicked,
                text = stringResource(id = R.string.create_an_account),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))

            OrSignInWithText()

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))

            SigninOptionsButton(
                onClick = onGoogleSignInClicked,
                text = stringResource(id = R.string.continue_with_google),
                icon = DesignSystemR.drawable.google_icon,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.already_have_an_account) + " ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(id = R.string.log_in),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))

            ByCreatingAccountSection()

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))
        }
    }
}

@Composable
fun ByCreatingAccountSection(modifier: Modifier = Modifier) {
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
            append(stringResource(id = R.string.by_creating_account) + " ")
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
fun PasswordRulesText(
    modifier: Modifier = Modifier,
    textColor: Color,
    textStyle: TextStyle,
    isMinLengthMet: Boolean,
    isLetterAndNumberMet: Boolean,
    isUpperCaseMet: Boolean,
    isSpecialCharMet: Boolean
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.spacing.md),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xxs)
    ) {
        Text(
            text = stringResource(id = R.string.your_password_must),
            style = textStyle,
            color = textColor
        )
        Text(
            text = stringResource(id = R.string.at_least_6_characters),
            style = textStyle.copy(
                textDecoration = if (isMinLengthMet) TextDecoration.LineThrough else TextDecoration.None
            ),
            color = if (isMinLengthMet) MaterialTheme.colorScheme.primary else textColor

        )
        Text(
            text = stringResource(id = R.string.include_letters_and_numbers),
            style = textStyle.copy(
                textDecoration = if (isLetterAndNumberMet) TextDecoration.LineThrough else TextDecoration.None
            ),
            color = if (isLetterAndNumberMet) MaterialTheme.colorScheme.primary else textColor

        )
        Text(
            text = stringResource(id = R.string.include_uppercase_letter),
            style = textStyle.copy(
                textDecoration = if (isUpperCaseMet) TextDecoration.LineThrough else TextDecoration.None
            ),
            color = if (isUpperCaseMet) MaterialTheme.colorScheme.primary else textColor

        )
        Text(
            text = stringResource(id = R.string.include_special_character),
            style = textStyle.copy(
                textDecoration = if (isSpecialCharMet) TextDecoration.LineThrough else TextDecoration.None
            ),
            color = if (isSpecialCharMet) MaterialTheme.colorScheme.primary else textColor

        )
    }
}

@Preview(name = "Default", showBackground = true, showSystemUi = true)
@Composable
private fun SignupScreenPreview() {
    TravioTheme {
        SignupScreenContent(
            state = SignupUiState(
                password = "Password123!",
                isMinLengthMet = true,
                isLetterAndNumberMet = true,
                isUpperCaseMet = true,
                isSpecialCharMet = true
            ),
            errorMessage = null,
            onCloseClicked = {},
            onFirstNameChange = {},
            onLastNameChange = {},
            onUsernameChange = {},
            onEmailChange = {},
            onPasswordChange = {},
            onPasswordVisibilityCheck = {},
            onSignupClicked = {},
            onGoogleSignInClicked = {},
            onNavigateToLogin = {}
        )
    }
}

@Preview(name = "Validation errors", showBackground = true, showSystemUi = true)
@Composable
private fun SignupScreenErrorPreview() {
    TravioTheme {
        SignupScreenContent(
            state = SignupUiState(
                firstname = "",
                email = "bad-email",
                isFirstNameError = true,
                isEmailError = true,
                isPasswordMismatch = true
            ),
            errorMessage = "Please fix the highlighted fields.",
            onCloseClicked = {},
            onFirstNameChange = {},
            onLastNameChange = {},
            onUsernameChange = {},
            onEmailChange = {},
            onPasswordChange = {},
            onPasswordVisibilityCheck = {},
            onSignupClicked = {},
            onGoogleSignInClicked = {},
            onNavigateToLogin = {}
        )
    }
}
