package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.player.domain.api.SaveLastTrackUseCase
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.repository.TrackHistoryRepository

class SaveLastTrackUseCaseImpl(
    private val repo: TrackHistoryRepository
) : SaveLastTrackUseCase {
    override fun execute(track: Track) = repo.saveLastPlayedTrack(track)
}