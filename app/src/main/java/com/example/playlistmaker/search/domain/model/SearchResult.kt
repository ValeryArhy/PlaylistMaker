package com.example.playlistmaker.search.domain.model

sealed class SearchResult {
    data class Success(val tracks: List<Track>) : SearchResult()
    data class Error(val message: String? = null) : SearchResult()
}