package com.example.feature.onboarding

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.designsystem.R
import com.example.domain.repository.prefernces.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(private val preferencesManager: PreferencesManager) :
    ViewModel() {

    private val _event = Channel<OnboardingEvent>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()
    val pages = listOf(
        PageModel(
            image = R.drawable.onboarding_3,
            title = R.string.onboarding_title_1,
            description = R.string.onboarding_description_1
        ),
        PageModel(
            image = R.drawable.onboarding_1,
            title = R.string.onboarding_title_2,
            description = R.string.onboarding_description_2
        ),
        PageModel(
            image = R.drawable.onboarding_2,
            title = R.string.onboarding_title_3,
            description = R.string.onboarding_description_3
        ),
    )

    var currentPage = mutableStateOf(0)

    fun onFinishClicked() {
        viewModelScope.launch {
            preferencesManager.setOnboardingComplete(complete = true)
            _event.send(OnboardingEvent.NavigateToStarterLogin)
        }
    }
}
