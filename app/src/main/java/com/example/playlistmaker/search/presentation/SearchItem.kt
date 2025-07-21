package com.example.playlistmaker.search.presentation

import com.example.playlistmaker.search.domain.model.Track

sealed class SearchItem {
    data class TrackItem(
        val track: Track
    ) : SearchItem()
}