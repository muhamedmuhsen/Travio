package com.example.feature.starterlogin

import androidx.compose.foundation.Image
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.dev.utils.auth.GoogleCredentialHelper
import com.example.designsystem.components.AppButton
import com.example.designsystem.components.ErrorSnackBar
import com.example.designsystem.components.SigninOptionsButton
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.spacing
import com.example.domain.utils.Result
import com.example.feature.onboarding.BuildConfig
import com.example.feature.onboarding.R
import kotlinx.coroutines.launch
@Composable
fun StarterLogin(
    modifier: Modifier = Modifier,
    viewModel: StarterLoginViewModel = hiltViewModel(),
    navigateToLogin: () -> Unit,
    navigateToSignup: () -> Unit,
    navigateToHome: () -> Unit = {},
    navigateToSurvey: () -> Unit = {}
) {
    val webClientId = BuildConfig.GOOGLE_WEB_CLIENT_ID
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
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
                StarterLoginEvent.NavigateToLogin -> {
                    navigateToLogin()
                }

                StarterLoginEvent.NavigateToSignup -> {
                    navigateToSignup()
                }

                StarterLoginEvent.GoogleSignIn -> {
                    /* handled via onClick */
                }

                StarterLoginEvent.FacebookSignIn -> {
                    /* TODO: facebook login */
                }
                StarterLoginEvent.NavigateToHome -> navigateToHome()
                StarterLoginEvent.NavigateToSurvey -> navigateToSurvey()
                is StarterLoginEvent.ShowAuthError -> {
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
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = MaterialTheme.spacing.lg)
        ) {
            Box(
                contentAlignment = Alignment.BottomCenter,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // Placeholder for the image collage as requested
                Image(
                    painter = painterResource(id = com.example.designsystem.R.drawable.starter_login),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    MaterialTheme.colorScheme.background
                                ),
                                startY = 400f
                            )
                        )
                )
                Text(
                    text = stringResource(id = R.string.your_journy_start_here),
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = MaterialTheme.spacing.lg)
                )
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.sm))

            AppButton(
                onClick = { viewModel.onCreateAccountClicked() },
                text = stringResource(id = R.string.create_an_account),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MaterialTheme.spacing.xxxl)
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))

            SigninOptionsButton(
                onClick = {
                    viewModel.onGoogleSignInStarted()
                    scope.launch {
                        when (
                            val result =
                                GoogleCredentialHelper.getGoogleIdToken(context, webClientId)
                        ) {
                            is Result.Success -> viewModel.onGoogleSignInResult(result.data)
                            is Result.Error -> viewModel.onGoogleSignInError(result.error)
                        }
                    }
                },
                text = stringResource(id = R.string.continue_with_google),
                icon = com.example.designsystem.R.drawable.google_icon,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))

            SigninOptionsButton(
                onClick = { /*TODO: facebook login*/ },
                text = stringResource(id = R.string.continue_with_facebook),
                icon = com.example.designsystem.R.drawable.facebook_icon,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.xl))
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
                    modifier = Modifier.clickable { viewModel.onLoginClicked() }
                )
            }
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))

            ByLoggingSection()

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun StarterLoginPreview() {
    TravioTheme { StarterLogin(navigateToLogin = {}, navigateToSignup = {}) }
}
