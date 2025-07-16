package com.example.playlistmaker.domain.models

sealed class SearchResult {
    data class Success(val tracks: List<Track>) : SearchResult()
    object Error : SearchResult()
}