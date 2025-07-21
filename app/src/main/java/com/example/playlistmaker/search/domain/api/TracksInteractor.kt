package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.model.Track

interface TracksInteractor {

    fun searchTracks(expression: String, consumer: TracksConsumer)


    fun loadHistory(consumer: TracksConsumer)


    fun saveTrack(track: Track)


    fun clearHistory()

    fun saveLastPlayedTrack(track: Track)
    fun getLastPlayedTrack(): Track?

    interface TracksConsumer {
        fun consume(tracks: List<Track>)
        fun onError(error: Throwable)
    }
}