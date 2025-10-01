package com.example.playlistmaker.player.domain.api

import com.example.playlistmaker.search.domain.model.Track

interface GetLastTrackUseCase {
    suspend fun execute(): Track?
}