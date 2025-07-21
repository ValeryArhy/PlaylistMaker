package com.example.playlistmaker.domain.impl

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.playlistmaker.domain.api.TrackHistoryRepository
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.SearchResult
import com.example.playlistmaker.domain.models.Track
import java.util.concurrent.Executors

class TracksInteractorImpl(
    private val tracksRepository: TracksRepository,
    private val historyRepository: TrackHistoryRepository
) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer) {
        Log.d("TracksInteractor", "Запущен поиск: $expression")
        executor.execute {
            try {
                val result = tracksRepository.searchTracks(expression)
                Log.d("TracksInteractor", "Результат поиска получен")
                Handler(Looper.getMainLooper()).post {
                    when (result) {
                        is SearchResult.Success -> {
                            Log.d("TracksInteractor", "Найдено: ${result.tracks.size}")
                            consumer.consume(result.tracks)
                        }
                        is SearchResult.Error -> {
                            Log.e("TracksInteractor", "Ошибка в результате поиска")
                            consumer.onError(Exception("Search failed"))
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("TracksInteractor", "Исключение при поиске", e)
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

    override fun saveLastPlayedTrack(track: Track) {
        executor.execute {
            historyRepository.saveLastPlayedTrack(track)
        }
    }

    override fun getLastPlayedTrack(): Track? {
        return historyRepository.getLastPlayedTrack()
    }
}
