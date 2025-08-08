package com.example.playlistmaker.search.domain.repository

import com.example.playlistmaker.search.domain.model.Track

interface TrackHistoryRepository {
    fun saveTrack(track: Track)
    fun getHistory(): List<Track>
    fun clearHistory()

    fun saveLastPlayedTrack(track: Track)
    fun getLastPlayedTrack(): Track?
}