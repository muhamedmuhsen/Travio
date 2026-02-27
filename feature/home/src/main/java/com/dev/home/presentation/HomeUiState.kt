package com.dev.home.presentation

import com.dev.home.components.CountryItem
import com.dev.home.components.RecentViewedUiState

sealed interface HomeUiState {
    object Loading : HomeUiState

    data class Success(
        val countries: List<CountryItem> = emptyList(),
        val recentItems: List<RecentViewedUiState> = emptyList(),
        val destinations: List<DestinationMock> = emptyList(),
        val recommended: List<DestinationMock> = emptyList()
    ) : HomeUiState

    data class Error(val message: String) : HomeUiState
}

data class DestinationMock(
    val title: String,
    val rating: Double,
    val reviewCount: Int,
    val description: String,
    val price: String,
    val imageUrl: String
)
