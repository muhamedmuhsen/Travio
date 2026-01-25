package com.example.feature.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecase.auth.LogoutUseCase
import com.example.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val logoutUseCase: LogoutUseCase) : ViewModel() {
    fun logout() {
        viewModelScope.launch {
            val result = logoutUseCase()
            Log.d("Logout", "Logout result: $result")

            when (result) {
                is Result.Error -> {
                    Log.d("Logout", "Logout failed")
                }

                is Result.Success -> {
                    Log.d("Logout", "Logout successful")
                    // Navigate to login screen
                }
            }
        }
    }
}