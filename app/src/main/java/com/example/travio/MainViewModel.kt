package com.example.travio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject constructor() : ViewModel() {
    private var _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()
    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination = _startDestination.asStateFlow()

    init {
        /* TODO: check if the user completed the onboarding steps*/
        /* TODO: check if the user is authenticated*/
        /* TODO: check if the user is authenticated and completed the survey steps*/

        _startDestination.value = Screen.OnboardingScreen.route
        _isLoading.value = false

    }
}