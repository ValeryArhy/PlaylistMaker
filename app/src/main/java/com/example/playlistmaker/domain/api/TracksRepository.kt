package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.SearchResult
import com.example.playlistmaker.domain.models.Track

interface TracksRepository {
    fun searchTracks(expression: String): SearchResult
}