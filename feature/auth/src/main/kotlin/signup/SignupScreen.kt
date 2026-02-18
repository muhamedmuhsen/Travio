package com.example.feature.signup

import android.widget.Toast
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
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.feature.auth.R
import com.example.designsystem.components.AppButton
import com.example.designsystem.components.AppTextField
import com.example.designsystem.components.SigninOptionsButton
import com.example.designsystem.components.TextFieldType
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.spacing
import com.example.feature.auth.BuildConfig
import com.example.feature.login.components.OrSignInWithText
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


    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                SignupEvent.NavigateToLogin -> navigateToLogin()
                SignupEvent.NavigateToHome -> navigateToHome()
                is SignupEvent.ShowAuthError -> {
                    Toast.makeText(
                        context,
                        event.message.asString(context),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                SignupEvent.NavigateToVerifyEmail -> navigateToVerifyEmail(uiState.value.email)
            }
        }
    }

    Scaffold(
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
                            contentDescription = stringResource(id = DesignSystemR.string.close),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }, actions = {
                    Spacer(modifier = Modifier.size(56.dp))
                }, colors = TopAppBarDefaults.topAppBarColors(
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
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))

            AppTextField(
                value = uiState.value.firstname,
                onValueChange = { viewModel.onFirstNameChange(it) },
                placeholder = stringResource(id = R.string.first_name),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))

            AppTextField(
                value = uiState.value.lastname,
                onValueChange = { viewModel.onLastNameChange(it) },
                placeholder = stringResource(id = R.string.last_name),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))

            AppTextField(
                value = uiState.value.username,
                onValueChange = { viewModel.onUsernameChange(it) },
                placeholder = stringResource(id = R.string.username),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))
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
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))

            PasswordRulesText(
                textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                textStyle = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))

            AppButton(
                onClick = {
                    viewModel.onSignupClicked()
                },
                text = stringResource(id = R.string.create_an_account),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))

            OrSignInWithText()

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))

            SigninOptionsButton(
                onClick = { viewModel.onGoogleSignInClicked(context, webClientId) },
                text = stringResource(id = R.string.continue_with_google),
                icon = DesignSystemR.drawable.google_icon,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.sm))

            SigninOptionsButton(
                onClick = {/* viewModel.onFacebookSigninClicked()*/ },
                text = stringResource(id = R.string.continue_with_facebook),
                icon = DesignSystemR.drawable.facebook_icon,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))


            Row(
                horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()
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
                    modifier = Modifier.clickable { navigateToLogin() })
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))
            AcknowledgementSection()
        }
    }

}

@Composable
fun AcknowledgementSection(modifier: Modifier = Modifier) {
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
    modifier: Modifier = Modifier, textColor: Color, textStyle: TextStyle
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
            text = stringResource(id = R.string.at_least_9_characters),
            style = textStyle,
            color = textColor

        )
        Text(
            text = stringResource(id = R.string.include_letters_and_numbers),
            style = textStyle,
            color = textColor

        )
        Text(
            text = stringResource(id = R.string.include_special_character),
            style = textStyle,
            color = textColor

        )
    }
}

@Preview
@Composable
private fun SignupScreenPreview() {
    TravioTheme {
        SignupScreen(onCloseClicked = {}, navigateToLogin = {}, navigateToHome = {}) {}
    }
}
