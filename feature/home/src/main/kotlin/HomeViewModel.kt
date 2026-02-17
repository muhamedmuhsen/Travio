package com.example.feature.home

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecase.auth.LogoutUseCase
import com.example.data.local.datastore.CredentialsManagerImpl
import com.example.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val credentialsManagerImpl: CredentialsManagerImpl
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    fun logout() {
        viewModelScope.launch {
            val result = logoutUseCase()

            when (result) {
                is Result.Error -> {
                }

                is Result.Success -> {
                }
            }
        }
    }


    fun onHomeClicked() {
        _state.update { homeState -> homeState.copy(selectedItem = 0) }
    }

    fun onFavoriteClicked() {
        _state.update { homeState -> homeState.copy(selectedItem = 1) }

    }

    fun onCommunityClicked() {
        _state.update { homeState -> homeState.copy(selectedItem = 2) }

    }

    fun onAiChatClicked() {
        _state.update { homeState -> homeState.copy(selectedItem = 3) }
    }

    fun onProfileClicked() {
        _state.update { homeState -> homeState.copy(selectedItem = 4) }
    }
}