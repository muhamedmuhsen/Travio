package com.dev.profile.profile_

import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.domain.repository.prefernces.PreferencesManager
import com.example.domain.usecase.auth.LogoutUseCase
import com.example.domain.usecase.preferences.ToggleDarkModeUseCase
import com.example.domain.usecase.user_management.GetUserUseCase
import com.example.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ui.state.UiState
import ui.text.asUiText
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val toggleDarkModeUseCase: ToggleDarkModeUseCase,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _event = Channel<ProfileEvent>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    init {
        observeTheme()
        loadProfileData()
    }

    private fun observeTheme() {
        viewModelScope.launch {
            preferencesManager.observeDarkMode().collect { isDarkMode ->
                _uiState.update { state ->
                    state.copy(isDarkMode = isDarkMode)
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.update { state -> state.copy(profileUiState = UiState.Loading) }
            when (val result = logoutUseCase()) {
                is Result.Error -> {
                    _uiState.update { state -> state.copy(profileUiState = UiState.Error(result.error.asUiText())) }
                    _event.send(ProfileEvent.ShowProfileError(result.error.asUiText()))
                }

                is Result.Success -> {
                    hideLogoutDialog()
                    // delete user data
                    _event.send(ProfileEvent.NavigateToLogin)
                }
            }
        }
    }

    private fun loadProfileData() {
        if (_uiState.value.profileUiState is UiState.Loading) return
        _uiState.update { state -> state.copy(profileUiState = UiState.Loading) }

        viewModelScope.launch {
            when (val result = getUserUseCase()) {
                is Result.Error -> {
                    _uiState.update { state ->
                        state.copy(profileUiState = UiState.Error(result.error.asUiText()))
                    }
                    _event.send(ProfileEvent.ShowProfileError(result.error.asUiText()))
                }

                is Result.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            profileUiState = UiState.Success(),
                            email = result.data.email,
                            firstName = result.data.firstName,
                            lastName = result.data.lastName,
                            profilePictureUrl = result.data.profilePictureUrl,
                        )
                    }
                }
            }
        }
    }

    fun onLogoutClicked() {
        _uiState.update { state -> state.copy(showLogoutDialog = true) }

    }

    fun hideLogoutDialog() {
        _uiState.update { state -> state.copy(showLogoutDialog = false) }
    }
    fun toggleDarkMode() {
        viewModelScope.launch { toggleDarkModeUseCase(!_uiState.value.isDarkMode) }
    }
}