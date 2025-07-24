package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.model.Track

interface TracksInteractor {
    fun searchTracks(expression: String, callback: (Result<List<Track>>) -> Unit)
    fun loadHistory(callback: (Result<List<Track>>) -> Unit)
    fun saveTrack(track: Track)
    fun clearHistory()
    fun saveLastPlayedTrack(track: Track)
    fun getLastPlayedTrack(): Track?
}