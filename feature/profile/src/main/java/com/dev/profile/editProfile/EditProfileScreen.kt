package com.dev.profile.editProfile

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.dev.utils.uitext.UiText
import com.example.designsystem.components.AppButton
import com.example.designsystem.components.AppTextField
import com.example.designsystem.components.ErrorSnackBar
import com.example.designsystem.components.TextFieldType
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.elevation
import com.example.designsystem.theme.spacing
import com.example.feature.profile.R
import timber.log.Timber

@Composable
fun EditProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: EditProfileViewModel = hiltViewModel(),
    onCloseClicked: () -> Unit,
    NavigateToProfile: (String?) -> Unit,
    profilePic: String?,
    firstName: String?,
    lastName: String?,
    username: String?
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.onProfileImageSelected(it.toString())
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            Timber.d("Image URI selected: $it")
        }
    }

    LaunchedEffect(Unit) {
        viewModel.setUserData(
            firstName = firstName,
            lastName = lastName,
            username = username,
            profilePicUri = profilePic
        )
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            kotlinx.coroutines.delay(3000)
            errorMessage = null
        }
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                EditProfileEvent.NavigateToProfile -> NavigateToProfile(state.profileImageUri)
                is EditProfileEvent.ShowProfileError -> {
                    errorMessage = event.message.asString(context)
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            errorMessage?.let { message ->
                ErrorSnackBar(text = message)
            }
        },
        topBar = { AppTopBar(onCloseClicked = onCloseClicked) }
    ) { innerPadding ->
        EditProfileContent(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            firstName = state.firstName,
            onFirstNameChange = viewModel::onFirstNameChange,
            isFirstNameError = state.isFirstNameError,
            lastName = state.lastName,
            onLastNameChange = viewModel::onLastNameChange,
            isLastNameError = state.isLastNameError,
            username = state.username,
            onUsernameChange = viewModel::onEmailChange,
            isUsernameError = state.isUsernameError,
            profileImageUri = state.profileImageUri,
            onProfileImageClick = { imagePickerLauncher.launch("image/*") },
            onUpdateClick = viewModel::saveProfile,
            firstNameErrorMessage = state.firstNameErrorMessage,
            lastNameErrorMessage = state.lastNameErrorMessage,
            usernameErrorMessage = state.usernameErrorMessage,
            onImageSuccess = viewModel::onProfileImageSelected
        )
    }
}

@Composable
fun EditProfileContent(
    modifier: Modifier = Modifier,
    firstName: String,
    onFirstNameChange: (String) -> Unit,
    firstNameErrorMessage: UiText?,
    isFirstNameError: Boolean,
    lastName: String,
    onLastNameChange: (String) -> Unit,
    lastNameErrorMessage: UiText?,
    isLastNameError: Boolean,
    username: String,
    onUsernameChange: (String) -> Unit,
    isUsernameError: Boolean,
    usernameErrorMessage: UiText?,
    onUpdateClick: () -> Unit,
    onProfileImageClick: () -> Unit,
    profileImageUri: String?,
    onImageSuccess: (String?) -> Unit
) {
    Column(
        modifier = modifier.padding(MaterialTheme.spacing.md),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ChangeProfilePictureBox(
                imageUri = profileImageUri,
                onClick = onProfileImageClick,
                onImageSuccess = onImageSuccess
            )
            AppTextField(
                value = firstName,
                onValueChange = onFirstNameChange,
                placeholder = stringResource(id = R.string.first_name_placeholder),
                isError = isFirstNameError,
                errorMessage = firstNameErrorMessage?.asString(),
                fieldType = TextFieldType.TEXT,
                modifier = Modifier.fillMaxWidth()
            )
            AppTextField(
                value = lastName,
                onValueChange = onLastNameChange,
                placeholder = stringResource(id = R.string.last_name_placeholder),
                isError = isLastNameError,
                errorMessage = lastNameErrorMessage?.asString(),
                fieldType = TextFieldType.TEXT,
                modifier = Modifier.fillMaxWidth()

            )
            AppTextField(
                value = username,
                onValueChange = onUsernameChange,
                placeholder = stringResource(id = R.string.username_placeholder),
                isError = isUsernameError,
                errorMessage = usernameErrorMessage?.asString(),
                fieldType = TextFieldType.EMAIL,
                modifier = Modifier.fillMaxWidth()

            )
        }
        AppButton(
            onClick = onUpdateClick,
            text = stringResource(id = R.string.update),
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ChangeProfilePictureBox(
    imageUri: String?,
    onClick: () -> Unit,
    onImageSuccess: (String?) -> Unit
) {
    Timber.d("imageUri: ${imageUri ?: "null"}")
    Box(
        modifier = Modifier.size(MaterialTheme.spacing.xxxl * 2),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(0f)
                .border(
                    width = MaterialTheme.elevation.xs,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    shape = CircleShape
                )
                .clip(CircleShape)
                .clickable(
                    onClick = onClick,
                    onClickLabel = stringResource(id = R.string.change_profile_picture)
                ),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUri ?: R.drawable.ic_default_profile)
                    .error(R.drawable.ic_default_profile).placeholder(R.drawable.ic_default_profile)
                    .crossfade(true).listener(onError = { _, result ->
                        Timber.e(result.throwable, "Failed to load image")
                    }, onSuccess = { _, _ ->
                        imageUri?.let { onImageSuccess(it) }
                    }).build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .zIndex(1f)
                .size(MaterialTheme.spacing.xl)
                .background(MaterialTheme.colorScheme.primary, CircleShape)
                .border(
                    width = MaterialTheme.elevation.xs,
                    color = MaterialTheme.colorScheme.surface,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.camera),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(MaterialTheme.spacing.md)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    modifier: Modifier = Modifier,
    onCloseClicked: () -> Unit
) {
    Column {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.edit_profile),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            },
            navigationIcon = {
                Box(
                    modifier = Modifier
                        .padding(start = MaterialTheme.spacing.md)
                        .size(MaterialTheme.spacing.xxl)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = CircleShape
                        )
                        .clickable { onCloseClicked() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(id = com.example.designsystem.R.string.close),
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
        HorizontalDivider(
            thickness = MaterialTheme.elevation.xs,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    }
}

@Preview
@Preview(name = "Dark", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun EditScreenPreview() {
    TravioTheme(dynamicColor = false) {
        Scaffold(
            topBar = { AppTopBar(onCloseClicked = {}) }
        ) { innerPadding ->
            EditProfileContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                firstName = "Osama",
                onFirstNameChange = {},
                firstNameErrorMessage = null,
                isFirstNameError = false,
                lastName = "Mahmoud",
                onLastNameChange = {},
                lastNameErrorMessage = null,
                isLastNameError = false,
                username = "osama@gmail.com",
                onUsernameChange = {},
                isUsernameError = false,
                usernameErrorMessage = null,
                onUpdateClick = {},
                onProfileImageClick = {},
                profileImageUri = null,
                onImageSuccess = {}
            )
        }
    }
}
