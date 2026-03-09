package com.dev.community.presentation

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dev.community.components.AddPhotoBox
import com.dev.community.components.ShareMomentTopBar
import com.dev.feature.community.R
import com.dev.utils.uistate.UiState
import com.example.designsystem.components.AppButton
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.spacing

@Composable
fun ShareMomentScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ShareMomentViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.onPhotoSelected(it.toString()) }
    }

    val photoRequiredMessage = stringResource(R.string.share_moment_error_photo_required)
    val locationRequiredMessage = stringResource(R.string.share_moment_error_location_required)

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                ShareMomentEvent.PostCreated -> onNavigateBack()
                ShareMomentEvent.ShowPhotoRequired ->
                    snackbarHostState.showSnackbar(photoRequiredMessage)

                ShareMomentEvent.ShowLocationRequired ->
                    snackbarHostState.showSnackbar(locationRequiredMessage)
            }
        }
    }

    ShareMomentScreenContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onPhotoClicked = { photoPickerLauncher.launch("image/*") },
        onLocationChanged = viewModel::onLocationChanged,
        onDescriptionChanged = viewModel::onDescriptionChanged,
        onPostClicked = viewModel::onPostClicked,
        onCloseClicked = onNavigateBack,
        modifier = modifier
    )
}

@Composable
fun ShareMomentScreenContent(
    state: ShareMomentUiState,
    onPhotoClicked: () -> Unit,
    onLocationChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onPostClicked: () -> Unit,
    onCloseClicked: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { ShareMomentTopBar(onCloseClicked = onCloseClicked) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = MaterialTheme.spacing.md)
                .padding(top = MaterialTheme.spacing.lg, bottom = MaterialTheme.spacing.xl),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.lg)
        ) {
            AddPhotoBox(
                photoUri = state.photoUri,
                onClick = onPhotoClicked,
                modifier = Modifier.fillMaxWidth()
            )

            LocationSection(
                location = state.location,
                onLocationChanged = onLocationChanged
            )

            DescriptionSection(
                description = state.description,
                onDescriptionChanged = onDescriptionChanged
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.xs))

            AppButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onPostClicked,
                text = stringResource(R.string.share_moment_post_button),
                isEnabled = state.submitState !is UiState.Loading,
                shape = MaterialTheme.shapes.medium
            )
        }
    }
}

@Composable
private fun LocationSection(
    location: String,
    onLocationChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
    ) {
        Text(
            text = stringResource(R.string.share_moment_location_label),
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        OutlinedTextField(
            value = location,
            onValueChange = onLocationChanged,
            placeholder = {
                Text(
                    text = stringResource(R.string.share_moment_location_hint),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.location_icon),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            },
            shape = MaterialTheme.shapes.large,
            singleLine = true,
            colors = shareMomentFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun DescriptionSection(
    description: String,
    onDescriptionChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
    ) {
        Text(
            text = stringResource(R.string.share_moment_description_label),
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChanged,
            placeholder = {
                Text(
                    text = stringResource(R.string.share_moment_description_hint),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            shape = MaterialTheme.shapes.large,
            minLines = 5,
            maxLines = 7,
            colors = shareMomentFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun shareMomentFieldColors() =
    TextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
        unfocusedIndicatorColor = MaterialTheme.colorScheme.outlineVariant,
        disabledIndicatorColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.38f),
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
        focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        cursorColor = MaterialTheme.colorScheme.primary
    )

@Preview(showBackground = true)
@Composable
private fun ShareMomentScreenEmptyPreview() {
    TravioTheme {
        ShareMomentScreenContent(
            state = ShareMomentUiState(),
            onPhotoClicked = {},
            onLocationChanged = {},
            onDescriptionChanged = {},
            onPostClicked = {},
            onCloseClicked = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ShareMomentScreenFilledPreview() {
    TravioTheme {
        ShareMomentScreenContent(
            state = ShareMomentUiState(
                photoUri = "https://images.unsplash.com/photo-1533105079780-92b9be482077?w=800",
                location = "Santorini, Greece",
                description = "The sunset views from Oia are breathtaking!"
            ),
            onPhotoClicked = {},
            onLocationChanged = {},
            onDescriptionChanged = {},
            onPostClicked = {},
            onCloseClicked = {}
        )
    }
}
