package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface TrackHistoryRepository {
    fun saveTrack(track: Track)
    fun getHistory(): List<Track>
    fun clearHistory()

    fun saveLastPlayedTrack(track: Track)
    fun getLastPlayedTrack(): Track?
}