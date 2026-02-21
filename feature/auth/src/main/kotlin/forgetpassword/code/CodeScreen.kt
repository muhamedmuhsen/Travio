package com.example.feature.forgetpassword.code

import androidx.compose.runtime.mutableStateOf

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.feature.auth.R
import com.example.designsystem.components.AppButton
import com.example.designsystem.components.ErrorSnackBar
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.spacing
import com.example.feature.code.CodeEvent
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodeScreen(
    modifier: Modifier = Modifier,
    viewModel: CodeViewModel = hiltViewModel(),
    navigateToResetPassword: () -> Unit,
    onBackClicked: () -> Unit,
    email: String,
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
        viewModel.startCountdown()
        viewModel.event.collect { event ->
            when (event) {
                CodeEvent.NavigateToResetPassword -> navigateToResetPassword()
                CodeEvent.OnBackClicked -> onBackClicked()
                is CodeEvent.ShowError -> {
                    errorMessage = event.message.asString(context)
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
                .padding(horizontal = MaterialTheme.spacing.lg)
        ) {
            Text(
                text = stringResource(id = R.string.please_enter_code),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Text(
                text = email,
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
                    onSendAgainClicked = { viewModel.onSendAgainClicked(email) }
                )
                CountdownTimer(state.timeLeft)
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))

            AppButton(
                onClick = { viewModel.onContinueClicked(email) },
                text = stringResource(id = R.string.continue_button),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = MaterialTheme.spacing.md)
            )
        }
    }
}

@Composable
fun CountdownTimer(timeLeft: Int) {
    Text(
        text = formatTime(timeLeft),
        style = MaterialTheme.typography.bodySmall,
    )
}

private fun formatTime(seconds: Int): String {

    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60

    return String.format(Locale.ROOT, "%02d:%02d", minutes, secs)
}
@Composable
fun SendAgain(
    onSendAgainClicked: () -> Unit, modifier: Modifier = Modifier
) {
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
            append(stringResource(id = R.string.didnot_get_it) + " ")
        }
        withStyle(highlightedStyle) {
            append(stringResource(id = R.string.send_new_code))
        }
    }
    Text(
        text = annotatedString,
        style = MaterialTheme.typography.bodySmall,
        textAlign = TextAlign.Center,
        modifier = modifier
            .clip(RoundedCornerShape(MaterialTheme.spacing.sm))
            .clickable { onSendAgainClicked() }
            .padding(MaterialTheme.spacing.xs))
}

@Composable
fun OtpInputField(
    modifier: Modifier = Modifier,
    otpLength: Int = 6,
    onOtpFilled: (String) -> Unit,
    isError: Boolean
) {
    var otpValue by remember { mutableStateOf("") }

    BasicTextField(
        value = otpValue,
        onValueChange = { value ->
            if (value.length <= otpLength && value.all { it.isDigit() }) {
                otpValue = value
                if (value.length == otpLength) {
                    onOtpFilled(value)
                }
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        decorationBox = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                modifier = modifier.fillMaxWidth()
            ) {
                repeat(otpLength) { index ->
                    OtpCell(
                        char = otpValue.getOrNull(index)?.toString() ?: "",
                        isFilled = index < otpValue.length,
                        isError = isError,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        })
}

@Composable
fun OtpCell(
    modifier: Modifier = Modifier, char: String = "", isFilled: Boolean, isError: Boolean
) {

    var borderColor = if (isFilled) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline
    }

    if (isError) borderColor = MaterialTheme.colorScheme.error


    val textColor = if (isFilled) {
        MaterialTheme.colorScheme.onBackground
    } else {
        MaterialTheme.colorScheme.outline
    }

    Box(
        modifier = modifier
            .height(52.dp)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(MaterialTheme.spacing.sm)
            ), contentAlignment = Alignment.Center
    ) {
        Text(
            text = char.ifEmpty { "0" },
            style = MaterialTheme.typography.headlineSmall,
            color = textColor
        )
    }
}

@Preview
@Composable
private fun CodeScreenPreview() {
    TravioTheme {
        CodeScreen(
            email = "mail@gmail.com",
            navigateToResetPassword = {},
            onBackClicked = {},
            viewModel = hiltViewModel()
        )
    }
}
