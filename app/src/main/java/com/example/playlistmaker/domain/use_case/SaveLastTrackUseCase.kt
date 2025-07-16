package com.example.playlistmaker.domain.use_case

import com.example.playlistmaker.domain.api.TrackHistoryRepository
import com.example.playlistmaker.domain.models.Track

class SaveLastTrackUseCase(private val repo: TrackHistoryRepository) {
    fun execute(track: Track) = repo.saveLastPlayedTrack(track)
}