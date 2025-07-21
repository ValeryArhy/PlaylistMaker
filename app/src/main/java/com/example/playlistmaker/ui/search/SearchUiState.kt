package com.example.playlistmaker.ui.search

import com.example.playlistmaker.domain.models.Track

sealed class SearchUiState {
    object Loading : SearchUiState()
    object Empty : SearchUiState()
    object Error : SearchUiState()
    data class Content(val tracks: List<Track>) : SearchUiState()
    data class History(val tracks: List<Track>) : SearchUiState()
}