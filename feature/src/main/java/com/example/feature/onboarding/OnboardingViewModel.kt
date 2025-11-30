package com.example.feature.onboarding

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.designsystem.R
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class OnboardingViewModel : ViewModel() {

    private val _event = Channel<OnboardingEvent>()
    val event = _event.receiveAsFlow()
    val pages = listOf(
        PageModel(
            R.drawable.onboarding_3,
            "Explore Amazing\nDestinations",
            "Discover beautiful places around the world and start your next adventure."
        ),
        PageModel(
            R.drawable.onboarding_1,
            "Find the Best\nHotels",
            "Book top-rated hotels that match your style, comfort, and budget."
        ),
        PageModel(
            R.drawable.onboarding_2,
            "Plan Your Perfect\nTrip",
            "Book top-rated hotels that match your style, comfort, and budget."
        ),
    )

    var currentPage = mutableStateOf(0)

    fun onFinishClicked() {
        viewModelScope.launch { _event.send(OnboardingEvent.NavigateToLogin) }
    }
}