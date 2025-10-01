package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {
    fun searchTracks(expression: String): Flow<List<Track>>
    fun loadHistory(): Flow<List<Track>>
    suspend fun saveTrack(track: Track)
    suspend fun clearHistory()
    suspend fun saveLastPlayedTrack(track: Track)
    suspend fun getLastPlayedTrack(): Track?
}