package com.example.playlistmaker.domain.use_case


import com.example.playlistmaker.domain.api.TrackHistoryRepository
import com.example.playlistmaker.domain.models.Track

class GetLastTrackUseCase(private val repo: TrackHistoryRepository) {
    fun execute(): Track? = repo.getLastPlayedTrack()
}