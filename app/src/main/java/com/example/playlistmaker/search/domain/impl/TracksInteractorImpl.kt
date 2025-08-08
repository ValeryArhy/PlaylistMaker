package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.repository.TrackHistoryRepository
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.repository.TracksRepository
import com.example.playlistmaker.search.domain.model.SearchResult
import com.example.playlistmaker.search.domain.model.Track
import java.util.concurrent.Executors

class TracksInteractorImpl(
    private val tracksRepository: TracksRepository,
    private val historyRepository: TrackHistoryRepository
) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, callback: (Result<List<Track>>) -> Unit) {
        executor.execute {
            try {
                when (val result = tracksRepository.searchTracks(expression)) {
                    is SearchResult.Success -> callback(Result.success(result.tracks))
                    is SearchResult.Error -> callback(Result.failure(Exception("Search failed")))
                }
            } catch (e: Exception) {
                callback(Result.failure(e))
            }
        }
    }

    override fun loadHistory(callback: (Result<List<Track>>) -> Unit) {
        executor.execute {
            try {
                val history = historyRepository.getHistory()
                callback(Result.success(history))
            } catch (e: Exception) {
                callback(Result.failure(e))
            }
        }
    }

    override fun saveTrack(track: Track) {
        executor.execute {
            historyRepository.saveTrack(track)
        }
    }

    override fun clearHistory() {
        executor.execute {
            historyRepository.clearHistory()
        }
    }

    override fun saveLastPlayedTrack(track: Track) {
        executor.execute {
            historyRepository.saveLastPlayedTrack(track)
        }
    }

    override fun getLastPlayedTrack(): Track? {
        return historyRepository.getLastPlayedTrack()
    }
}