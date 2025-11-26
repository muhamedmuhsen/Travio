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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.designsystem.R
import com.example.designsystem.components.AppButton
import com.example.designsystem.components.AppTextField
import com.example.designsystem.components.SigninOptionsButton
import com.example.designsystem.components.TextFieldType
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
    onCloseClicked: () -> Unit,
) {

    val uiState = viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                LoginEvent.NavigateToForgotPassword -> TODO()
                LoginEvent.NavigateToHome -> TODO()
                LoginEvent.NavigateToSignup -> TODO()
                is LoginEvent.ShowAuthError -> TODO()
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
                        .padding(start = 16.dp)
                        .size(40.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = CircleShape
                        )
                        .clickable { onCloseClicked() }, contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
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
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(id = R.string.continue_using_your),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(20.dp))

            AppTextField(
                value = uiState.value.email,
                onValueChange = { viewModel.onEmailChange(it) },
                placeholder = stringResource(id = R.string.email_or_user_name),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                value = uiState.value.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                placeholder = stringResource(id = R.string.password),
                fieldType = TextFieldType.PASSWORD,
                isPasswordVisible = uiState.value.isPasswordVisible,
                onPasswordVisibilityChecked = { viewModel.onPasswordVisibilityCheck() },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = MaterialTheme.spacing.lg)
            ) {
                Text(
                    text = stringResource(id = R.string.forgot_password),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { viewModel.onForgotPasswordClicked() })
            }

            AppButton(
                onClick = { viewModel.onLoginClicked(uiState.value.email, uiState.value.password) },
                text = stringResource(id = R.string.log_in),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            OrSignInWithText()

            Spacer(modifier = Modifier.height(24.dp))

            SigninOptionsButton(
                onClick = {},
                text = stringResource(id = R.string.continue_with_google),
                icon = R.drawable.google_icon,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            SigninOptionsButton(
                onClick = {},
                text = stringResource(id = R.string.continue_with_facebook),
                icon = R.drawable.facebook_icon,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            ByLoggingSection()
            Spacer(modifier = Modifier.height(24.dp))

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

            Spacer(modifier = Modifier.height(24.dp))
        }
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
        modifier = modifier.padding(horizontal = 8.dp)
    )
}


@Composable
fun OrSignInWithText(modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
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
            onCloseClicked = {})
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
            onCloseClicked = {})
    }
}

