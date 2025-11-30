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
            R.drawable.onboarding_1,
            "Explore Amazing\nDestinations",
            "Discover beautiful places around the world and start your next adventure."
        ),
        PageModel(
            R.drawable.onboarding_2,
            "Find the Best\n" + "Hotels",
            "Book top-rated hotels that match your style, comfort, and budget."
        ),
        PageModel(
            R.drawable.onboarding_3,
            "Plan Your Perfect\n" + "Trip",
            "Book top-rated hotels that match your style, comfort, and budget."
        ),
    )

    var currentPage = mutableStateOf(0)

    fun onFinish() {
        viewModelScope.launch { _event.send(OnboardingEvent.NavigateToLogin) }
    }
}