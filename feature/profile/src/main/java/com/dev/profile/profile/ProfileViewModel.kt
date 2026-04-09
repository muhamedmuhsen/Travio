package com.dev.profile.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.onboarding.language.AppLocaleManager
import com.dev.utils.localization.AppLanguage
import com.dev.utils.uistate.UiState
import com.dev.utils.uitext.asUiText
import com.example.domain.repository.prefernces.PreferencesManager
import com.example.domain.usecase.auth.session.LogoutUseCase
import com.example.domain.usecase.preferences.ToggleDarkModeUseCase
import com.example.domain.usecase.usermanagement.GetUserUseCase
import com.example.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val toggleDarkModeUseCase: ToggleDarkModeUseCase,
    private val preferencesManager: PreferencesManager,
    private val appLocaleManager: AppLocaleManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _event = Channel<ProfileEvent>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    init {
        observeTheme()
        detectCurrentLanguage()
        loadProfileData()
    }

    private fun detectCurrentLanguage() {
        val isArabic = Locale.getDefault().language == "ar"
        _uiState.update { state -> state.copy(isArabic = isArabic) }
    }

    private fun observeTheme() {
        viewModelScope.launch {
            preferencesManager.observeDarkModeNullable().collect { isDarkMode ->
                if (isDarkMode != null) {
                    _uiState.update { state -> state.copy(isDarkMode = isDarkMode) }
                }
            }
        }
    }

    fun initSystemDarkMode(isSystemDark: Boolean) {
        viewModelScope.launch {
            val hasExplicitPref = preferencesManager.observeDarkModeNullable()
            hasExplicitPref.collect { stored ->
                if (stored == null) {
                    _uiState.update { state -> state.copy(isDarkMode = isSystemDark) }
                }
                // only need first emission
                return@collect
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

    fun loadProfileData() {
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
                            username = result.data.username.orEmpty(),
                            profilePictureUrl = result.data.profilePictureUrl
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

    fun toggleLanguage() {
        val isCurrentlyArabic = _uiState.value.isArabic
        val newLanguage = if (isCurrentlyArabic) AppLanguage.ENGLISH else AppLanguage.ARABIC
        _uiState.update { state -> state.copy(isArabic = !isCurrentlyArabic) }
        appLocaleManager.changeLanguage(newLanguage)
    }

    fun updateProfileImage(imageUri: String?) {
        _uiState.update { state -> state.copy(profilePictureUrl = imageUri) }
    }
}
