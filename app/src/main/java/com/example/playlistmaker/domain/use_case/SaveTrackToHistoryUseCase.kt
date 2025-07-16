package com.example.playlistmaker.domain.use_case

import com.example.playlistmaker.domain.api.TrackHistoryRepository
import com.example.playlistmaker.domain.models.Track

class SaveTrackToHistoryUseCase(private val repo: TrackHistoryRepository) {
    fun execute(track: Track) = repo.saveTrack(track)
}