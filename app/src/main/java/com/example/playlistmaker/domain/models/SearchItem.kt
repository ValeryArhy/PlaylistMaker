package com.example.playlistmaker.domain.models

sealed class SearchItem {
    data class TrackItem(
        val track: Track
    ) : SearchItem()
}