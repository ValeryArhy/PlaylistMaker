package com.example.playlistmaker.domain.use_case

import com.example.playlistmaker.domain.api.TrackHistoryRepository

class ClearTrackHistoryUseCase(private val repo: TrackHistoryRepository) {
    fun execute() = repo.clearHistory()
}