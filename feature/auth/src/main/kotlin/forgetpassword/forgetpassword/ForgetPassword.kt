package com.example.feature.forgetpassword

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.designsystem.components.AppButton
import com.example.designsystem.components.AppTextField
import com.example.designsystem.components.ErrorSnackBar
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.spacing
import com.example.feature.auth.R
import com.example.feature.forgetpassword.forgetpassword.ForgetPasswordEvent
import com.example.feature.forgetpassword.forgetpassword.ForgetPasswordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgetPasswordScreen(
    modifier: Modifier = Modifier,
    onCloseClicked: () -> Unit,
    viewModel: ForgetPasswordViewModel = hiltViewModel(),
    navigateToCodeScreen: (email: String) -> Unit
) {
    val uiState = viewModel.state.collectAsStateWithLifecycle()
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
                ForgetPasswordEvent.NavigateToCodeScreen -> navigateToCodeScreen(uiState.value.email)
                ForgetPasswordEvent.OnBackClicked -> onCloseClicked()
                is ForgetPasswordEvent.ShowError -> errorMessage = event.message.asString(context)
            }
        }
    }

    ForgetPasswordScreenContent(
        state = uiState.value,
        errorMessage = errorMessage,
        onEmailChange = viewModel::onEmailChange,
        onContinueClicked = viewModel::onContinueClicked,
        onCloseClicked = viewModel::onCloseClicked,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgetPasswordScreenContent(
    state: com.example.feature.forgetpassword.forgetpassword.ForgetPasswordState,
    errorMessage: String?,
    onEmailChange: (String) -> Unit,
    onContinueClicked: () -> Unit,
    onCloseClicked: () -> Unit,
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
                        text = stringResource(id = R.string.forgot_password),
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
                .padding(horizontal = MaterialTheme.spacing.lg),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier.height(MaterialTheme.spacing.lg))
                Text(
                    text = stringResource(R.string.reset_your_password_using),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier.height(MaterialTheme.spacing.lg))

                AppTextField(
                    value = state.email,
                    onValueChange = onEmailChange,
                    isError = state.isEmailError,
                    placeholder = stringResource(id = R.string.email),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier.height(MaterialTheme.spacing.lg))

                Text(
                    text = stringResource(R.string.enter_the_email_you_used),
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier.height(MaterialTheme.spacing.lg))

                AppButton(
                    onClick = onContinueClicked,
                    text = stringResource(id = R.string.continue_button),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            ContactSection(modifier = Modifier.padding(bottom = MaterialTheme.spacing.md))
        }
    }
}

@Composable
fun ContactSection(modifier: Modifier = Modifier) {
    val annotatedString = buildAnnotatedString {
        val regularStyle = SpanStyle(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = MaterialTheme.typography.bodySmall.fontSize
        )
        val highlightedStyle = SpanStyle(
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium,
            fontSize = MaterialTheme.typography.bodySmall.fontSize
        )

        withStyle(regularStyle) {
            append(stringResource(id = R.string.if_you_dont_remember_email_prefix))
        }
        withStyle(highlightedStyle) {
            append(stringResource(id = R.string.contact_us))
        }
    }
    Text(
        text = annotatedString,
        style = MaterialTheme.typography.bodySmall,
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.spacing.xs)
    )
}

@Preview(name = "Default", showBackground = true, showSystemUi = true)
@Composable
private fun ForgetPasswordPreview() {
    TravioTheme {
        ForgetPasswordScreenContent(
            state = com.example.feature.forgetpassword.forgetpassword.ForgetPasswordState(),
            errorMessage = null,
            onEmailChange = {},
            onContinueClicked = {},
            onCloseClicked = {}
        )
    }
}

@Preview(name = "Error — invalid email", showBackground = true, showSystemUi = true)
@Composable
private fun ForgetPasswordErrorPreview() {
    TravioTheme {
        ForgetPasswordScreenContent(
            state = com.example.feature.forgetpassword.forgetpassword.ForgetPasswordState(
                email = "not-an-email",
                isEmailError = true
            ),
            errorMessage = "No account found with this email.",
            onEmailChange = {},
            onContinueClicked = {},
            onCloseClicked = {}
        )
    }
}
