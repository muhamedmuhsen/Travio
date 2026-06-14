package com.example.feature.forgetpassword.newpassword

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.designsystem.components.AppButton
import com.example.designsystem.components.AppTextField
import com.example.designsystem.components.ErrorSnackBar
import com.example.designsystem.components.TextFieldType
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.spacing
import com.example.feature.auth.R
import com.example.feature.newpassword.NewPasswordState
import com.example.feature.signup.PasswordRulesText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPasswordScreen(
    modifier: Modifier = Modifier,
    viewModel: NewPasswordViewModel = hiltViewModel(),
    navigateToLogin: () -> Unit,
    onCloseClicked: () -> Unit,
    email: String
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
                NewPasswordEvent.NavigateToLogin -> navigateToLogin()
                NewPasswordEvent.OnClosedClicked -> onCloseClicked()
                is NewPasswordEvent.ShowError -> {
                    errorMessage = event.message.asString(context)
                }
            }
        }
    }

    NewPasswordScreenContent(
        state = uiState.value,
        errorMessage = errorMessage,
        onPasswordChange = viewModel::onPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onPasswordVisibilityCheck = viewModel::onPasswordVisibilityCheck,
        onConfirmPasswordVisibilityChanged = viewModel::onConfirmPasswordVisibilityChanged,
        onResetPasswordClicked = { viewModel.onResetPasswordClicked(email) },
        onCloseClicked = onCloseClicked,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPasswordScreenContent(
    state: NewPasswordState,
    errorMessage: String?,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onPasswordVisibilityCheck: () -> Unit,
    onConfirmPasswordVisibilityChanged: () -> Unit,
    onResetPasswordClicked: () -> Unit,
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
                        text = stringResource(id = R.string.reset_password),
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
                    Spacer(modifier = Modifier.size(MaterialTheme.spacing.xxxl))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = MaterialTheme.spacing.lg)
        ) {
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))
            AppTextField(
                value = state.newPassword,
                onValueChange = onPasswordChange,
                placeholder = stringResource(R.string.new_password),
                fieldType = TextFieldType.PASSWORD,
                isError = state.isPasswordsDoesnotMatch,
                isPasswordVisible = state.isPasswordVisible,
                onPasswordVisibilityChecked = onPasswordVisibilityCheck,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))
            PasswordRulesText(
                textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                textStyle = MaterialTheme.typography.bodySmall,
                isMinLengthMet = state.isMinLengthMet,
                isLetterAndNumberMet = state.isLetterAndNumberMet,
                isUpperCaseMet = state.isUpperCaseMet,
                isSpecialCharMet = state.isSpecialCharMet
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))

            AppTextField(
                value = state.confirmNewPassword,
                onValueChange = onConfirmPasswordChange,
                placeholder = stringResource(R.string.confirm_new_password),
                fieldType = TextFieldType.PASSWORD,
                isPasswordVisible = state.isConfirmPasswordVisible,
                isError = state.isPasswordsDoesnotMatch,
                onPasswordVisibilityChecked = onConfirmPasswordVisibilityChanged,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))

            AppButton(
                onClick = onResetPasswordClicked,
                text = stringResource(id = R.string.reset_your_password_button),
                isLoading = state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(name = "Default", showBackground = true, showSystemUi = true)
@Composable
private fun NewPasswordScreenPreview() {
    TravioTheme {
        NewPasswordScreenContent(
            state = NewPasswordState(),
            errorMessage = null,
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onPasswordVisibilityCheck = {},
            onConfirmPasswordVisibilityChanged = {},
            onResetPasswordClicked = {},
            onCloseClicked = {}
        )
    }
}

@Preview(name = "Error — passwords do not match", showBackground = true, showSystemUi = true)
@Composable
private fun NewPasswordScreenErrorPreview() {
    TravioTheme {
        NewPasswordScreenContent(
            state = NewPasswordState(
                newPassword = "Password1!",
                confirmNewPassword = "Password2!",
                isPasswordsDoesnotMatch = true
            ),
            errorMessage = "Passwords do not match.",
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onPasswordVisibilityCheck = {},
            onConfirmPasswordVisibilityChanged = {},
            onResetPasswordClicked = {},
            onCloseClicked = {}
        )
    }
}
