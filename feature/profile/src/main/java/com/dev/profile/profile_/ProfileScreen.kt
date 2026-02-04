package com.dev.profile.profile_

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.dev.profile.profile_.components.LogoutButton
import com.dev.profile.profile_.components.ProfileOption
import com.dev.profile.profile_.components.ProfileOptionWithSwitch
import com.dev.profile.profile_.components.TopSection
import com.dev.profile.profile_.components.TopSectionWithSwitch
import com.example.designsystem.theme.TravioTheme
import com.example.feature.profile.R

@Composable
fun ProfileScreen(
    onNavigateToDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
    ) { innerPadding ->
        ProfileContent(
            uiState = uiState,
            onNavigateToDetail = onNavigateToDetail,
            logout = viewModel::logout,
            toggleDarkMode = viewModel::toggleDarkMode,
            modifier = modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun ProfileContent(
    uiState: ProfileUiState,
    onNavigateToDetail: (String) -> Unit,
    logout: () -> Unit,
    toggleDarkMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()

    ) {
        // Header Section
        SectionHeader(
            userName = "${uiState.firstName} ${uiState.lastName}".trim().ifEmpty { "User" },
            userEmail = uiState.email,
            profileImageUrl = uiState.profilePictureUrl
        )

        // Scrollable Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Account Settings Section
            ProfileCategory(
                title = "ACCOUNT SETTINGS", options = listOf(
                    ProfileOption(
                        "My profile", R.drawable.person
                    ) { onNavigateToDetail("profile") }, ProfileOption(
                        "Addresses", R.drawable.location
                    ) { onNavigateToDetail("address") })
            )

            // Preferences Section
            ProfileCategoryWithSwitch(
                title = "PREFERENCES",
                clickableOptions = listOf(
                    ProfileOption(
                        "Language", R.drawable.language
                    ) { onNavigateToDetail("language") }), switchOptions = listOf(
                    ProfileOptionWithSwitch(
                        "Dark Mode", R.drawable.light_mode, isChecked = uiState.isDarkMode
                    ) { toggleDarkMode() })
            )

            // Support Section
            ProfileCategory(
                title = "SUPPORT & HELP", options = listOf(
                    ProfileOption(
                        "Help Center", R.drawable.help_centeer
                    ) { onNavigateToDetail("help") })
            )

            // Logout Button
            LogoutButton(
                modifier = Modifier.fillMaxWidth(), onClick = logout
            )
        }
    }
}

@Composable
fun ProfileCategory(
    title: String, options: List<ProfileOption>, modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(start = 4.dp)
        )
        TopSection(options = options)
    }
}

@Composable
fun ProfileCategoryWithSwitch(
    title: String,
    clickableOptions: List<ProfileOption>,
    switchOptions: List<ProfileOptionWithSwitch>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.outline,
        )
        TopSectionWithSwitch(
            clickableOptions = clickableOptions, switchOptions = switchOptions
        )
    }
}

@Composable
fun SectionHeader(
    userName: String, userEmail: String, profileImageUrl: String?, modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(profileImageUrl ?: R.drawable.ic_default_profile) // Use Coil for both!
                    .crossfade(true).build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Column {
            Text(
                text = userName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                text = userEmail,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    TravioTheme {
        ProfileContent(
            uiState = ProfileUiState(
                firstName = "Osama",
                lastName = "Mahmoud",
                email = "osama.mahmoud00@gmail.com",
                isDarkMode = false
            ), onNavigateToDetail = {}, logout = {}, toggleDarkMode = {})
    }
}