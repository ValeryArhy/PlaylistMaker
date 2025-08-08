package com.example.playlistmaker.player.domain.usecase


import com.example.playlistmaker.search.domain.repository.TrackHistoryRepository
import com.example.playlistmaker.search.domain.model.Track

class GetLastTrackUseCase(private val repo: TrackHistoryRepository) {
    fun execute(): Track? = repo.getLastPlayedTrack()
}