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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.designsystem.R
import com.example.designsystem.theme.TravioTheme
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = hiltViewModel(), onFinish: () -> Unit = {}
) {
    val pages = viewModel.pages
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                OnboardingEvent.NavigateToLogin -> onFinish()
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
                state = pagerState, modifier = Modifier.fillMaxSize()
            ) { currentPage ->
                val page = pages[currentPage]

                // Box to hold the image and the gradient/text overlay for each page
                Box(modifier = Modifier.fillMaxSize()) {
                    // Background Image
                    Image(
                        painter = painterResource(page.image),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )

                    // Gradient overlay with Text on top
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent, Color.Black.copy(alpha = 0.8f)
                                    ), startY = 400f
                                )
                            )
                    ) {
                        // Title and Description
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomStart)
                                .padding(24.dp)
                        ) {
                            Text(
                                text = page.title,
                                style = MaterialTheme.typography.headlineLarge,
                                color = Color.White // Use a static color for visibility
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = page.description,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = Color.White.copy(alpha = 0.9f), // Use a static color
                            )
                            // Space to prevent overlap with the navigation row
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }

            // LAYER 2: UI Controls (drawn on top of the pager)
            // Skip button
            if (pagerState.currentPage < pages.size - 1) {
                Text(
                    text = "Skip",
                    modifier = Modifier
                        .align(Alignment.TopStart) // Better placement
                        .padding(16.dp)
                        .padding(top = 36.dp)
                        .clickable { onFinish() },
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            // Page indicators and Next button
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth() // Ensure it spans the width
                    .padding(24.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                // Page Indicators
                repeat(pages.size) { index ->
                    Box(
                        modifier = Modifier
                            .width(if (pagerState.currentPage == index) 24.dp else 8.dp)
                            .height(8.dp)
                            .clip(CircleShape)
                            .background(
                                if (pagerState.currentPage == index) MaterialTheme.colorScheme.secondaryContainer
                                else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                            )
                    )
                    if (index < pages.size - 1) {
                        Spacer(modifier = Modifier.width(8.dp))
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
                    }, modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = MaterialTheme.shapes.large
                        )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.arrow),
                        contentDescription = "Next",
                        modifier = Modifier.size(24.dp)
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
        OnboardingScreen()
    }
}