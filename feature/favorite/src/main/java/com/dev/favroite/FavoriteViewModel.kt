package com.dev.favroite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Place
import com.example.domain.repository.favorite.FavoritePlaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val favoritePlaceRepository: FavoritePlaceRepository
) : ViewModel() {

    val _state = MutableStateFlow(FavoriteState())
    val state = _state.asStateFlow()
    fun insertDummyData() {
        viewModelScope.launch {
            val dummyPlaces = listOf(
                Place(
                    id = 1,
                    name = "Eiffel Tower",
                    description = "Famous iron tower in Paris",
                    imageUrl = "https://example.com/eiffel.jpg",
                    rating = 4.8f
                ),
                Place(
                    id = 2,
                    name = "Colosseum",
                    description = "Ancient amphitheater in Rome",
                    imageUrl = "https://example.com/colosseum.jpg",
                    rating = 4.7f
                ),
                Place(
                    id = 3,
                    name = "Big Ben",
                    description = "Iconic clock tower in London",
                    imageUrl = "https://example.com/bigben.jpg",
                    rating = 4.6f
                )
            )

            dummyPlaces.forEach { place ->
                favoritePlaceRepository.addPlaceToFavorite(place)
            }
        }
    }

    fun deleteitem() {
        viewModelScope.launch {
            favoritePlaceRepository.deletePlaceFromFavorite("2")
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
