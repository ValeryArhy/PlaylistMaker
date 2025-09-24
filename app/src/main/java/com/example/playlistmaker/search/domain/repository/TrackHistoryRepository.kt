package com.example.playlistmaker.search.domain.repository

import com.example.playlistmaker.search.domain.model.Track

interface TrackHistoryRepository {
    suspend fun saveTrack(track: Track)
    suspend fun getHistory(): List<Track>
    suspend fun clearHistory()
    suspend fun saveLastPlayedTrack(track: Track)
    suspend fun getLastPlayedTrack(): Track?
}
