package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.player.domain.api.GetLastTrackUseCase
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.repository.TrackHistoryRepository

class GetLastTrackUseCaseImpl(
    private val repo: TrackHistoryRepository
) : GetLastTrackUseCase {
    override fun execute(): Track? = repo.getLastPlayedTrack()
}