package com.dev.community.presentation

import android.location.Geocoder
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dev.feature.community.R
import com.example.designsystem.theme.TravioTheme
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import java.util.Locale

private val DEFAULT_LOCATION = LatLng(30.0444, 31.2357) // Cairo

@Composable
fun LocationPickerScreen(
    onLocationSelected: (String) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedLatLng by remember { mutableStateOf<LatLng?>(null) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(DEFAULT_LOCATION, 10f)
    }
    val context = LocalContext.current

    LocationPickerScaffold(
        showConfirmFab = selectedLatLng != null,
        onConfirmClicked = {
            selectedLatLng?.let { latLng ->
                val locationName = reverseGeocode(context, latLng)
                onLocationSelected(locationName)
            }
        },
        onNavigateBack = onNavigateBack,
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapClick = { latLng -> selectedLatLng = latLng }
            ) {
                selectedLatLng?.let { latLng ->
                    Marker(
                        state = MarkerState(position = latLng),
                        title = stringResource(R.string.location_picker_pin_title)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LocationPickerScaffold(
    showConfirmFab: Boolean,
    onConfirmClicked: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (androidx.compose.foundation.layout.PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.location_picker_title),
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.location_picker_back_cd)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            if (showConfirmFab) {
                FloatingActionButton(
                    onClick = onConfirmClicked,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = stringResource(R.string.location_picker_confirm_cd),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        },
        content = content
    )
}

@Suppress("DEPRECATION")
private fun reverseGeocode(
    context: android.content.Context,
    latLng: LatLng
): String {
    return try {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        if (!addresses.isNullOrEmpty()) {
            val address = addresses[0]
            buildString {
                address.locality?.let { append(it) }
                address.adminArea?.let {
                    if (isNotEmpty()) append(", ")
                    append(it)
                }
                address.countryName?.let {
                    if (isNotEmpty()) append(", ")
                    append(it)
                }
            }.ifEmpty { "${latLng.latitude}, ${latLng.longitude}" }
        } else {
            "${latLng.latitude}, ${latLng.longitude}"
        }
    } catch (_: Exception) {
        "${latLng.latitude}, ${latLng.longitude}"
    }
}

@Preview(name = "No Pin Selected", showBackground = true)
@Composable
private fun LocationPickerNoPinPreview() {
    TravioTheme {
        LocationPickerScaffold(
            showConfirmFab = false,
            onConfirmClicked = {},
            onNavigateBack = {}
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Map placeholder — tap to drop a pin",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(name = "Pin Selected", showBackground = true)
@Composable
private fun LocationPickerPinSelectedPreview() {
    TravioTheme {
        LocationPickerScaffold(
            showConfirmFab = true,
            onConfirmClicked = {},
            onNavigateBack = {}
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📍 Pin dropped on map",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
