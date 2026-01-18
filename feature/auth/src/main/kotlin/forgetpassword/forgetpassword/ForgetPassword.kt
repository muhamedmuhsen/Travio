package com.example.feature.forgetpassword

import android.widget.Toast
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.feature.auth.R
import com.example.designsystem.components.AppButton
import com.example.designsystem.components.AppTextField
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgetPasswordScreen(
    modifier: Modifier = Modifier, onCloseClicked: () -> Unit,
    viewModel: ForgetPasswordViewModel = hiltViewModel(),
    navigateToCodeScreen: () -> Unit
) {
    val uiState = viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                ForgetPasswordEvent.ContactUs -> TODO()
                ForgetPasswordEvent.NavigateToCodeScreen -> {
                    navigateToCodeScreen()
                }
                ForgetPasswordEvent.OnBackClicked -> {
                    onCloseClicked()
                }

                is ForgetPasswordEvent.ShowError -> {
                    Toast.makeText(context, event.message.asString(context), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
    Scaffold(
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
                }, navigationIcon = {
                    Box(
                        modifier = Modifier
                            .padding(start = MaterialTheme.spacing.md)
                            .size(MaterialTheme.spacing.xxl)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                shape = CircleShape
                            )
                            .clickable { viewModel.onCloseClicked() },
                        contentAlignment = Alignment.Center
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
                    value = uiState.value.email,
                    onValueChange = { viewModel.onEmailChange(it) },
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
                    onClick = { viewModel.onContinueClicked() },
                    text = stringResource(id = R.string.continue_button),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            ContactSection(
                modifier = Modifier.padding(bottom = MaterialTheme.spacing.md)
            )
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

@Preview
@Composable
private fun ForgetPasswordPreview() {
    TravioTheme {
        ForgetPasswordScreen(onCloseClicked = {}) {}
    }
}
