package com.example.playlistmaker.player.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.db.FavoriteTracksInteractor
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class FavoritesViewModel(
    private val favoriteTracksInteractor: FavoriteTracksInteractor
) : ViewModel() {

    val favoriteTracks: StateFlow<List<Track>> =
        favoriteTracksInteractor.favoriteTracks()
            .map {it }
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}