package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.repository.TrackHistoryRepository
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.repository.TracksRepository
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class TracksInteractorImpl(
    private val tracksRepository: TracksRepository,
    private val historyRepository: TrackHistoryRepository
) : TracksInteractor {

    override fun searchTracks(expression: String): Flow<List<Track>> =
        tracksRepository.searchTracks(expression)
            .flowOn(Dispatchers.IO)

    override fun loadHistory(): Flow<List<Track>> = flow {
        emit(historyRepository.getHistory())
    }.flowOn(Dispatchers.IO)

    override suspend fun saveTrack(track: Track) {
        historyRepository.saveTrack(track)
    }

    override suspend fun clearHistory() {
        historyRepository.clearHistory()
    }

    override suspend fun saveLastPlayedTrack(track: Track) {
        historyRepository.saveLastPlayedTrack(track)
    }

    override suspend fun getLastPlayedTrack(): Track? {
        return historyRepository.getLastPlayedTrack()
    }
}