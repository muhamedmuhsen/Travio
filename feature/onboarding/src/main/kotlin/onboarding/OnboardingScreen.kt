package com.example.feature.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dev.utils.localization.AppLanguage
import com.example.designsystem.R
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.spacing
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = hiltViewModel(),
    onFinish: () -> Unit
) {
    val pages = viewModel.pages
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    val locale = LocalConfiguration.current.locale
    val language = if (locale.language == "ar") AppLanguage.ARABIC else AppLanguage.ENGLISH

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                OnboardingEvent.NavigateToStarterLogin -> onFinish()
            }
        }
    }
    Scaffold { innerPadding ->
        // Main container that holds all layers
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // LAYER 1: The Pager with images and text (at the bottom of the stack)
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { currentPage ->
                val page = pages[currentPage]

                // Box to hold the image and the gradient/text overlay for each page
                Box(modifier = Modifier.fillMaxSize()) {
                    // Background Image
                    Image(
                        painter = painterResource(page.image),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Gradient overlay with Text on top
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.8f)
                                    ),
                                    startY = 400f
                                )
                            )
                    ) {
                        // Title and Description
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomStart)
                                .padding(MaterialTheme.spacing.lg)
                        ) {
                            Text(
                                text = stringResource(id = page.title),
                                style = MaterialTheme.typography.headlineLarge,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(MaterialTheme.spacing.sm))
                            Text(
                                text = stringResource(id = page.description),
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = Color.White.copy(alpha = 0.9f)
                            )
                            // Space to prevent overlap with the navigation row
                            Spacer(modifier = Modifier.height(MaterialTheme.spacing.xxxl))
                        }
                    }
                }
            }
            if (pagerState.currentPage < pages.size - 1) {
                Text(
                    text = stringResource(id = R.string.skip),
                    modifier = Modifier
                        .align(Alignment.TopStart) // Better placement
                        .padding(MaterialTheme.spacing.md)
                        .padding(top = 36.dp)
                        .clickable { viewModel.onFinishClicked() },
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
            }

            // Page indicators and Next button
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth() // Ensure it spans the width
                    .padding(MaterialTheme.spacing.lg),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Page Indicators
                repeat(pages.size) { index ->
                    Box(
                        modifier = Modifier
                            .width(if (pagerState.currentPage == index) MaterialTheme.spacing.lg else MaterialTheme.spacing.xs)
                            .height(MaterialTheme.spacing.xs)
                            .clip(CircleShape)
                            .background(
                                if (pagerState.currentPage == index) {
                                    MaterialTheme.colorScheme.secondaryContainer
                                } else {
                                    MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                                }
                            )
                    )
                    if (index < pages.size - 1) {
                        Spacer(modifier = Modifier.width(MaterialTheme.spacing.xs))
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Next/Finish button
                IconButton(
                    onClick = {
                        scope.launch {
                            if (pagerState.currentPage < pages.size - 1) {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            } else {
                                viewModel.onFinishClicked()
                            }
                        }
                    },
                    modifier = Modifier
                        .size(MaterialTheme.spacing.xxxl)
                        .background(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = MaterialTheme.shapes.large
                        )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.arrow),
                        contentDescription = stringResource(id = R.string.next),
                        modifier = Modifier
                            .size(MaterialTheme.spacing.lg)
                            .scale(
                                scaleX = if (language == AppLanguage.ARABIC) -1f else 1f,
                                scaleY = 1f
                            )
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun OnboardingPreview() {
    TravioTheme {
        OnboardingScreen {}
    }
}
