package com.example.feature.verifyEmail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.designsystem.components.AppButton
import com.example.designsystem.components.ErrorSnackBar
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.spacing
import com.example.feature.auth.R
import com.example.feature.forgetpassword.code.CountdownTimer
import com.example.feature.forgetpassword.code.OtpInputField
import com.example.feature.forgetpassword.code.SendAgain

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyEmailScreen(
    modifier: Modifier = Modifier,
    navigateToHome: () -> Unit,
    onBackClicked: () -> Unit,
    viewModel: VerifyEmailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()
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
                VerifyEmailEvent.NavigateToSuccess -> navigateToHome()
                VerifyEmailEvent.OnBackClicked -> onBackClicked()
                is VerifyEmailEvent.ShowError -> {
                    errorMessage = event.message.asString(context)
                }
            }
        }
    }

    VerifyEmailScreenContent(
        state = state,
        errorMessage = errorMessage,
        onBackClicked = viewModel::onBackClicked,
        onOtpFilled = viewModel::onCodeChange,
        onSendAgainClicked = viewModel::onSendAgainClicked,
        onContinueClicked = viewModel::onContinueClicked,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyEmailScreenContent(
    state: VerifyEmailState,
    errorMessage: String?,
    onBackClicked: () -> Unit,
    onOtpFilled: (String) -> Unit,
    onSendAgainClicked: () -> Unit,
    onContinueClicked: () -> Unit,
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
                title = {},
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .padding(start = MaterialTheme.spacing.md)
                            .size(MaterialTheme.spacing.xxl)
                            .clip(CircleShape)
                            .background(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .clickable { onBackClicked() },
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
                .padding(horizontal = MaterialTheme.spacing.lg)
        ) {
            Text(
                text = stringResource(id = R.string.please_enter_code),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = state.email,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))

            OtpInputField(
                onOtpFilled = onOtpFilled,
                isError = state.isCodeError
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SendAgain(onSendAgainClicked = onSendAgainClicked)
                CountdownTimer(state.timeLeft)
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))

            AppButton(
                onClick = onContinueClicked,
                text = stringResource(id = R.string.continue_button),
                isLoading = state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = MaterialTheme.spacing.md)
            )
        }
    }
}

@Preview(name = "Default", showBackground = true, showSystemUi = true)
@Composable
private fun VerifyEmailScreenPreview() {
    TravioTheme {
        VerifyEmailScreenContent(
            state = VerifyEmailState(email = "user@example.com", timeLeft = 59),
            errorMessage = null,
            onBackClicked = {},
            onOtpFilled = {},
            onSendAgainClicked = {},
            onContinueClicked = {}
        )
    }
}

@Preview(name = "Error — invalid code", showBackground = true, showSystemUi = true)
@Composable
private fun VerifyEmailScreenErrorPreview() {
    TravioTheme {
        VerifyEmailScreenContent(
            state = VerifyEmailState(email = "user@example.com", isCodeError = true, timeLeft = 0),
            errorMessage = "Invalid verification code. Please try again.",
            onBackClicked = {},
            onOtpFilled = {},
            onSendAgainClicked = {},
            onContinueClicked = {}
        )
    }
}
