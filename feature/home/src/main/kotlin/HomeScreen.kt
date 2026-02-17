package com.example.feature.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.designsystem.components.AppBottomBar

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(bottomBar = {
        AppBottomBar(
            onHomeClicked = viewModel::onHomeClicked,
            onFavoriteClicked = viewModel::onFavoriteClicked,
            onCommunityClicked = viewModel::onCommunityClicked,
            onAiChatClicked = viewModel::onAiChatClicked,
            onProfileClicked = viewModel::onProfileClicked,
            selectedItem = state.selectedItem
        )
    }
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding),

            ) {
            Text("Hi")
            Button(onClick = {
                viewModel.logout()
            }) {
                Text("Logout")
            }
        }
    }
}