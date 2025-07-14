package com.example.playlistmaker.domain.impl

import android.os.Handler
import android.os.Looper
import com.example.playlistmaker.domain.api.TrackHistoryRepository
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Track
import java.util.concurrent.Executors

class TracksInteractorImpl(
    private val tracksRepository: TracksRepository,
    private val historyRepository: TrackHistoryRepository
) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer) {
        executor.execute {
            try {
                val result = tracksRepository.searchTracks(expression)
                Handler(Looper.getMainLooper()).post {
                    consumer.consume(result)
                }
            } catch (e: Exception) {
                Handler(Looper.getMainLooper()).post {
                    consumer.onError(e)
                }
            }
        }
    }

    override fun loadHistory(consumer: TracksInteractor.TracksConsumer) {
        executor.execute {
            try {
                val history = historyRepository.getHistory()
                Handler(Looper.getMainLooper()).post {
                    consumer.consume(history)
                }
            } catch (e: Exception) {
                Handler(Looper.getMainLooper()).post {
                    consumer.onError(e)
                }
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
}