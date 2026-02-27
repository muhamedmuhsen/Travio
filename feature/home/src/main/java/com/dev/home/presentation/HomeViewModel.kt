package com.dev.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.home.components.CountryItem
import com.dev.home.components.RecentViewedUiState
import com.example.feature.home.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            // Simulate network delay
            delay(1500)
            _uiState.value = HomeUiState.Success(
                countries = mockCountries,
                recentItems = mockRecentItems,
                destinations = mockDestinations,
                recommended = mockRecommended
            )
        }
    }

    // Mock data moved from Composable
    private val mockCountries = listOf(
        CountryItem(
            "Egypt",
            "https://images.unsplash.com/photo-1503177119275-0aa32b3a9368?q=80&w=1000&auto=format&fit=crop"
        ),
        CountryItem(
            "Saudi Arabia",
            "https://images.unsplash.com/photo-1586724230021-4c3d3a91bfac?q=80&w=1000&auto=format&fit=crop"
        ),
        CountryItem(
            "Japan",
            "https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?q=80&w=1000&auto=format&fit=crop"
        )
    )

    private val mockRecentItems = listOf(
        RecentViewedUiState(
            description = "The warm rays of the setting sun in Africa bathe the savanna in golden light.",
            rating = 3.7f,
            reviewCount = 418,
            imageRes = R.drawable.card_placeholder_preview
        )
    )

    private val mockDestinations = listOf(
        DestinationMock(
            title = "Egypt",
            rating = 4.7,
            reviewCount = 1121,
            description = "Oasis Middle of the desert, with salt lakes, and Bedouin vibes.",
            price = "EGP 1100/ adult",
            imageUrl = "https://images.unsplash.com/photo-1503177119275-0aa32b3a9368?q=80&w=1000&auto=format&fit=crop"
        ),
        DestinationMock(
            title = "Japan",
            rating = 4.9,
            reviewCount = 840,
            description = "Historic temples, organized streets, and Japanese gardens.",
            price = "EGP 2350/ adult",
            imageUrl = "https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?q=80&w=1000&auto=format&fit=crop"
        )
    )

    private val mockRecommended = listOf(
        DestinationMock(
            title = "Egypt",
            rating = 4.7,
            reviewCount = 1121,
            description = "Oasis Middle of the desert, with salt lakes, and Bedouin vibes.",
            price = "EGP 1100/ adult",
            imageUrl = "https://images.unsplash.com/photo-1503177119275-0aa32b3a9368?q=80&w=1000&auto=format&fit=crop"
        ),
        DestinationMock(
            title = "Japan",
            rating = 4.9,
            reviewCount = 840,
            description = "Historic temples, organized streets, and Japanese gardens.",
            price = "EGP 2350/ adult",
            imageUrl = "https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?q=80&w=1000&auto=format&fit=crop"
        )
    )
}
