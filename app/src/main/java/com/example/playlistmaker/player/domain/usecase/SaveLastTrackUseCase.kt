package com.example.playlistmaker.player.domain.usecase

import com.example.playlistmaker.search.domain.repository.TrackHistoryRepository
import com.example.playlistmaker.search.domain.model.Track

class SaveLastTrackUseCase(private val repo: TrackHistoryRepository) {
    fun execute(track: Track) = repo.saveLastPlayedTrack(track)
}