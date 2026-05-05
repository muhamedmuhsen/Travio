package com.dev.profile.profile

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.dev.profile.profile.components.LogoutButton
import com.dev.profile.profile.components.LogoutDialog
import com.dev.profile.profile.components.ProfileLanguageButton
import com.dev.profile.profile.components.ProfileOption
import com.dev.profile.profile.components.ProfileOptionWithSwitch
import com.dev.profile.profile.components.TopSection
import com.dev.profile.profile.components.TopSectionWithSwitch
import com.example.designsystem.components.AppBottomBar
import com.example.designsystem.components.ErrorSnackBar
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.spacing
import com.example.feature.profile.R

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    onNavigateToDetail: (NavigationData) -> Unit,
    navigateToHome: () -> Unit = {},
    navigateToFavorite: () -> Unit = {},
    navigateToCommunity: () -> Unit = {},
    navigateToAi: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentBackStackEntry = navController.currentBackStackEntry
    val profileUpdated =
        currentBackStackEntry?.savedStateHandle?.getStateFlow("profile_updated", false)
    val context = LocalContext.current
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            kotlinx.coroutines.delay(3000)
            errorMessage = null
        }
    }

    val isSystemDark = isSystemInDarkTheme()

    LaunchedEffect(Unit) {
        viewModel.initSystemDarkMode(isSystemDark)
        viewModel.event.collect { event ->
            when (event) {
                is ProfileEvent.ShowProfileError -> {
                    errorMessage = event.message.asString(context)
                }

                ProfileEvent.NavigateToLogin -> {
                    /* handled elsewhere */
                }

                ProfileEvent.NavigateToEditProfile -> {
                    /* handled elsewhere */
                }

                ProfileEvent.NavigateToChangeLanguage -> {
                    /* handled elsewhere */
                }

                is ProfileEvent.ToggleDarkMode -> {
                    /* handled elsewhere */
                }
            }
        }
    }

    LaunchedEffect(profileUpdated?.value) {
        if (profileUpdated?.value == true) {
            viewModel.loadProfileData()
            currentBackStackEntry.savedStateHandle.remove<Boolean>("profile_updated")
        }
    }

    ShouldShowLogoutDialog(
        showLogoutDialog = uiState.showLogoutDialog,
        onCancel = viewModel::hideLogoutDialog,
        onConfirm = viewModel::logout
    )
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerHigh),
        snackbarHost = {
            errorMessage?.let { message ->
                ErrorSnackBar(text = message)
            }
        },
        bottomBar = {
            AppBottomBar(
                selectedItem = 4,
                onItemSelected = { index ->
                    when (index) {
                        0 -> navigateToHome()
                        1 -> navigateToFavorite()
                        2 -> navigateToCommunity()
                        3 -> navigateToAi()
                        4 -> {
                            /* already on Profile */
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        ProfileContent(
            uiState = uiState,
            onNavigateToDetail = onNavigateToDetail,
            showLogoutDialog = viewModel::onLogoutClicked,
            toggleDarkMode = viewModel::toggleDarkMode,
            toggleLanguage = viewModel::toggleLanguage,
            modifier = modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun ProfileContent(
    uiState: ProfileUiState,
    onNavigateToDetail: (NavigationData) -> Unit,
    showLogoutDialog: () -> Unit,
    toggleDarkMode: () -> Unit,
    toggleLanguage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Header Section
        SectionHeader(
            userName = "${uiState.firstName} ${uiState.lastName}".trim()
                .ifEmpty { stringResource(id = R.string.user_default) },
            userEmail = uiState.email,
            profileImageUrl = uiState.profilePictureUrl
        )

        // Scrollable Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(MaterialTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.lg)
        ) {
            // Account Settings Section
            ProfileCategory(
                title = stringResource(id = R.string.account_settings),
                options = listOf(
                    ProfileOption(
                        stringResource(id = R.string.my_profile),
                        R.drawable.person
                    ) {
                        onNavigateToDetail(
                            NavigationData(
                                firstname = uiState.firstName,
                                lastname = uiState.lastName,
                                username = uiState.username,
                                profilePicUri = uiState.profilePictureUrl
                            )
                        )
                    },
                    ProfileOption(
                        stringResource(id = R.string.addresses),
                        R.drawable.location
                    ) {
                    }
                )
            )

            // Preferences Section
            ProfileCategoryWithSwitch(
                title = stringResource(id = R.string.preferences),
                clickableOptions = listOf(
                    ProfileOption(
                        stringResource(id = R.string.language),
                        R.drawable.language,
                        trailingContent = {
                            ProfileLanguageButton(
                                text = if (uiState.isArabic) "ع" else "EN"
                            )
                        }
                    ) { toggleLanguage() }
                ),
                switchOptions = listOf(
                    ProfileOptionWithSwitch(
                        stringResource(id = R.string.dark_mode),
                        R.drawable.dark_mode,
                        isChecked = uiState.isDarkMode
                    ) { toggleDarkMode() }
                )
            )

            // Support Section
            ProfileCategory(
                title = stringResource(id = R.string.support_help),
                options = listOf(
                    ProfileOption(
                        stringResource(id = R.string.help_center),
                        R.drawable.help_centeer
                    ) { }
                )
            )

            // Logout Button
            LogoutButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = showLogoutDialog
            )
        }
    }
}

@Composable
fun ShouldShowLogoutDialog(
    showLogoutDialog: Boolean,
    onCancel: () -> Unit,
    onConfirm: () -> Unit
) {
    if (showLogoutDialog) {
        LogoutDialog(
            onDismiss = onCancel,
            onConfirm = onConfirm
        )
    }
}

@Composable
fun ProfileCategory(
    title: String,
    options: List<ProfileOption>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
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
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.outline
        )
        TopSectionWithSwitch(
            clickableOptions = clickableOptions,
            switchOptions = switchOptions
        )
    }
}

@Composable
fun SectionHeader(
    userName: String,
    userEmail: String,
    profileImageUrl: String?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = MaterialTheme.spacing.md, vertical = MaterialTheme.spacing.lg),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)
    ) {
        Box(
            modifier = Modifier
                .size(MaterialTheme.spacing.xxxl + MaterialTheme.spacing.xs)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(profileImageUrl)
                    .error(R.drawable.ic_default_profile).placeholder(R.drawable.ic_default_profile)
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
                color = Color.White
            )
            Text(
                text = userEmail,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.6f)
            )
        }
    }
}

@Preview(showBackground = true)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    TravioTheme(dynamicColor = false) {
        ProfileContent(
            uiState = ProfileUiState(
                firstName = "Osama",
                lastName = "Mahmoud",
                email = "osama.mahmoud00@gmail.com",
                isDarkMode = false
            ),
            onNavigateToDetail = {},
            showLogoutDialog = {},
            toggleDarkMode = {},
            toggleLanguage = {}
        )
    }
}
