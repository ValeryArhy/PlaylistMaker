package com.example.playlistmaker.search.domain.model

sealed class SearchResult {
    data class Success(val tracks: List<Track>) : SearchResult()
    object Error : SearchResult()
}