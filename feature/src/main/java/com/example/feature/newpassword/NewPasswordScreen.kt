package com.example.feature.newpassword

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.designsystem.R
import com.example.designsystem.components.AppButton
import com.example.designsystem.components.AppTextField
import com.example.designsystem.components.TextFieldType
import com.example.designsystem.theme.spacing
import com.example.feature.signup.PasswordRulesText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPasswordScreen(
    modifier: Modifier = Modifier,
    viewModel: NewPasswordViewModel = hiltViewModel(),
    onCloseClicked: () -> Unit
) {
    val uiState = viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
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
                            modifier = Modifier.size(MaterialTheme.spacing.lg)
                        )
                    }
                }, actions = {
                    Spacer(modifier = Modifier.size(MaterialTheme.spacing.xxxl))
                }, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = MaterialTheme.spacing.lg)
        ) {
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))
            AppTextField(
                value = uiState.value.newPassword,
                onValueChange = { viewModel.onPasswordChange(it) },
                placeholder = stringResource(R.string.new_password),
                fieldType = TextFieldType.PASSWORD,
                isError = uiState.value.isPasswordsDoesnotMatch,
                isPasswordVisible = uiState.value.isPasswordVisible,
                onPasswordVisibilityChecked = { viewModel.onPasswordVisibilityCheck() },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))
            PasswordRulesText(
                textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                textStyle = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))

            AppTextField(
                value = uiState.value.confirmNewPassword,
                onValueChange = { viewModel.onConfirmPasswordChange(it) },
                placeholder = stringResource(R.string.confirm_new_password),
                fieldType = TextFieldType.PASSWORD,
                isPasswordVisible = uiState.value.isConfirmPasswordVisible,
                isError = uiState.value.isPasswordsDoesnotMatch,
                onPasswordVisibilityChecked = { viewModel.onConfirmPasswordVisibilityChanged() },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))

            AppButton(
                onClick = { viewModel.onResetPasswordClicked() },
                text = stringResource(id = R.string.reset_your_password_button),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
