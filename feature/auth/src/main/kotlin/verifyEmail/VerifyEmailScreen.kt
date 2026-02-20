package com.example.feature.verifyEmail

import android.widget.Toast
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.designsystem.components.AppButton
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
    viewModel: VerifyEmailViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                VerifyEmailEvent.NavigateToSuccess -> navigateToHome()
                VerifyEmailEvent.OnBackClicked -> onBackClicked()
                is VerifyEmailEvent.ShowError -> {
                    Toast.makeText(
                        context,
                        event.message.asString(context),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    Scaffold(
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
                            .clickable { viewModel.onBackClicked() },
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
        }) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(horizontal = MaterialTheme.spacing.lg)
        ) {
            Text(
                text = stringResource(id = R.string.please_enter_code),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Text(
                text = state.email,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))

            OtpInputField(
                onOtpFilled = { viewModel.onCodeChange(it) },
                isError = state.isCodeError,
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SendAgain(
                    onSendAgainClicked = { viewModel.onSendAgainClicked() }
                )
                CountdownTimer(state.timeLeft)
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))

            AppButton(
                onClick = { viewModel.onContinueClicked() },
                text = stringResource(id = R.string.continue_button),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = MaterialTheme.spacing.md)
            )
        }
    }
}