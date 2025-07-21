package com.example.playlistmaker.search.presentation

import com.example.playlistmaker.search.domain.model.Track

sealed class SearchUiState {
    object Loading : SearchUiState()
    object Empty : SearchUiState()
    object Error : SearchUiState()
    data class Content(val tracks: List<Track>) : SearchUiState()
    data class History(val tracks: List<Track>) : SearchUiState()
}