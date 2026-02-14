package com.dev.profile.editProfile

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.designsystem.components.AppButton
import com.example.designsystem.components.AppTextField
import com.example.designsystem.components.TextFieldType
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.spacing
import com.example.feature.profile.R
import kotlinx.coroutines.flow.compose
import ui.text.UiText

@Composable
fun EditProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: EditProfileViewModel = hiltViewModel(),
    onCloseClicked: () -> Unit,
    NavigateToProfile: (String?) -> Unit,
    profilePic: String?
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.onProfileImageSelected(it.toString())
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            Log.d("EditProfileScreen", "Image URI selected: $it")
        }
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                EditProfileEvent.NavigateToProfile -> NavigateToProfile(state.profileImageUri)
                is EditProfileEvent.ShowProfileError -> {
                    Toast.makeText(context, event.message.asString(context), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
    InitProfilePic(viewModel, profilePic)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
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
            email = state.email,
            onEmailChange = viewModel::onEmailChange,
            isEmailError = state.isEmailError,
            profileImageUri = state.profileImageUri,
            onProfileImageClick = { imagePickerLauncher.launch("image/*") },
            onUpdateClick = viewModel::updateProfile,
            firstNameErrorMessage = state.firstNameErrorMessage,
            lastNameErrorMessage = state.lastNameErrorMessage,
            emailErrorMessage = state.emailErrorMessage,
            onImageSuccess = viewModel::onProfileImageSelected
        )
    }
}

@Composable
private fun InitProfilePic(
    viewModel: EditProfileViewModel,
    profilePic: String?
) {
    viewModel.onProfileImageSelected(profilePic)
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
    email: String,
    onEmailChange: (String) -> Unit,
    isEmailError: Boolean,
    emailErrorMessage: UiText?,
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
                onImageSuccess = onImageSuccess,
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
                value = email,
                onValueChange = onEmailChange,
                placeholder = stringResource(id = R.string.email_address_placeholder),
                isError = isEmailError,
                errorMessage = emailErrorMessage?.asString(),
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
    Log.d("imageUri", imageUri ?: "null")
    Box(
        modifier = Modifier.size(96.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(0f)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    shape = CircleShape
                )
                .clip(CircleShape)
                .clickable(
                    onClick = onClick,
                    onClickLabel = stringResource(id = R.string.change_profile_picture)
                ), contentAlignment = Alignment.Center
        ) {

            AsyncImage(
                model = ImageRequest
                    .Builder(LocalContext.current)
                    .data(imageUri ?: R.drawable.ic_default_profile)
                    .error(R.drawable.ic_default_profile)
                    .placeholder(R.drawable.ic_default_profile)
                    .crossfade(true)
                    .listener(
                        onError = { _, result ->
                            Log.e("AsyncImage", "Failed to load image", result.throwable)
                        },
                        onSuccess = { _, _ ->
                            imageUri?.let { onImageSuccess(it) }
                        }
                    )
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .zIndex(1f)
                .size(32.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape)
                .border(
                    width = 2.dp, color = MaterialTheme.colorScheme.surface, shape = CircleShape
                ), contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.camera),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(16.dp)
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
                        contentDescription = stringResource(id = com.example.designsystem.R.string.close),
                        modifier = Modifier.size(MaterialTheme.spacing.lg)
                    )
                }
            }, actions = {
                Spacer(modifier = Modifier.size(MaterialTheme.spacing.xxxl))
            }, colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )
        HorizontalDivider(
            thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    }
}


@Preview
@Composable
private fun EditScreenPreview() {
    TravioTheme {
        EditProfileScreen(
            profilePic = "",
            onCloseClicked = {},
            NavigateToProfile = {}
        )
    }
}