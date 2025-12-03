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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.designsystem.R
import com.example.designsystem.components.AppButton
import com.example.designsystem.components.SigninOptionsButton
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.spacing
import com.example.feature.login.ByLoggingSection

@Composable
fun StarterLogin(
    modifier: Modifier = Modifier,
    viewModel: StarterLoginViewModel = hiltViewModel(),
    navigateToLogin: () -> Unit,
    navigateToSignup: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                StarterLoginEvent.NavigateToLogin -> {
                    navigateToLogin()
                }

                StarterLoginEvent.NavigateToSignup -> {
                    navigateToSignup()
                }

                StarterLoginEvent.GoogleSignIn -> TODO()
                StarterLoginEvent.FacebookSignIn -> TODO()
            }
        }
    }
    Scaffold() { innerPadding ->
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
                    .weight(1f) // Use weight to take up available space
            ) {
                // Placeholder for the image collage as requested
                Image(
                    painter = painterResource(id = R.drawable.starter_login),
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
                                    Color.Transparent, MaterialTheme.colorScheme.background
                                ),
                                startY = 400f // Adjust this value to control where the fade starts
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
                onClick = { viewModel.onSocialLoginClicked(SocialType.GOOGLE) },
                text = stringResource(id = R.string.continue_with_google),
                icon = R.drawable.google_icon,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))

            SigninOptionsButton(
                onClick = { viewModel.onSocialLoginClicked(SocialType.FACEBOOK) },
                text = stringResource(id = R.string.continue_with_facebook),
                icon = R.drawable.facebook_icon,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.xl))
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
                    modifier = Modifier.clickable { viewModel.onLoginClicked() })
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
    TravioTheme() { StarterLogin(navigateToLogin = {}) {} }
}
